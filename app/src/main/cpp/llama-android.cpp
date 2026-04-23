#include <android/log.h>
#include <jni.h>
#include <unistd.h>

#include <algorithm>
#include <mutex>
#include <string>
#include <vector>

#include "llama.h"

#define TAG "LlamaAndroid"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

namespace {

std::mutex g_mutex;
llama_model * g_model = nullptr;
llama_context * g_ctx = nullptr;
bool g_backend_initialized = false;

constexpr int DEFAULT_CONTEXT_SIZE = 2048;
constexpr int DEFAULT_MAX_TOKENS = 512;
constexpr float DEFAULT_TEMPERATURE = 0.7f;
constexpr int MIN_THREADS = 1;
constexpr int MAX_THREADS = 8;
constexpr int THREAD_HEADROOM = 2;

int clamp_threads() {
    const long cores = sysconf(_SC_NPROCESSORS_ONLN);
    if (cores <= 0) {
        return 4;
    }
    return std::max(MIN_THREADS, std::min(MAX_THREADS, static_cast<int>(cores) - THREAD_HEADROOM));
}

void unload_model_locked() {
    if (g_ctx != nullptr) {
        llama_free(g_ctx);
        g_ctx = nullptr;
    }
    if (g_model != nullptr) {
        llama_free_model(g_model);
        g_model = nullptr;
    }
}

std::vector<llama_token> tokenize_prompt(const llama_vocab * vocab, const std::string & prompt) {
    std::vector<llama_token> tokens(prompt.size() + 8);
    int32_t count = llama_tokenize(
        vocab,
        prompt.c_str(),
        static_cast<int32_t>(prompt.size()),
        tokens.data(),
        static_cast<int32_t>(tokens.size()),
        true,
        true
    );

    if (count < 0) {
        tokens.resize(static_cast<size_t>(-count));
        count = llama_tokenize(
            vocab,
            prompt.c_str(),
            static_cast<int32_t>(prompt.size()),
            tokens.data(),
            static_cast<int32_t>(tokens.size()),
            true,
            true
        );
    }

    if (count < 0) {
        return {};
    }

    tokens.resize(static_cast<size_t>(count));
    return tokens;
}

}  // namespace

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_aethernovax_droidlocalai_engine_LlamaEngine_loadModel(
    JNIEnv * env,
    jobject /* thiz */,
    jstring model_path,
    jint context_size
) {
    if (model_path == nullptr) {
        return JNI_FALSE;
    }

    std::lock_guard<std::mutex> lock(g_mutex);

    if (!g_backend_initialized) {
        llama_backend_init();
        g_backend_initialized = true;
    }

    unload_model_locked();

    const char * path = env->GetStringUTFChars(model_path, nullptr);
    if (path == nullptr) {
        return JNI_FALSE;
    }

    LOGI("Loading GGUF model: %s", path);

    llama_model_params model_params = llama_model_default_params();
    g_model = llama_model_load_from_file(path, model_params);
    env->ReleaseStringUTFChars(model_path, path);

    if (g_model == nullptr) {
        LOGE("Failed to load model");
        return JNI_FALSE;
    }

    llama_context_params ctx_params = llama_context_default_params();
    ctx_params.n_ctx = context_size > 0 ? static_cast<uint32_t>(context_size) : DEFAULT_CONTEXT_SIZE;
    ctx_params.n_batch = 512;
    ctx_params.n_ubatch = 512;
    ctx_params.n_threads = clamp_threads();
    ctx_params.n_threads_batch = ctx_params.n_threads;

    g_ctx = llama_init_from_model(g_model, ctx_params);
    if (g_ctx == nullptr) {
        LOGE("Failed to create llama context");
        unload_model_locked();
        return JNI_FALSE;
    }

    LOGI("Model loaded successfully with context size %d", ctx_params.n_ctx);
    return JNI_TRUE;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_aethernovax_droidlocalai_engine_LlamaEngine_generateResponse(
    JNIEnv * env,
    jobject /* thiz */,
    jstring prompt,
    jint max_tokens,
    jfloat temperature,
    jobject on_token_received
) {
    std::lock_guard<std::mutex> lock(g_mutex);

    if (g_model == nullptr || g_ctx == nullptr || prompt == nullptr) {
        return env->NewStringUTF("Error: Model not loaded");
    }

    const char * raw_prompt = env->GetStringUTFChars(prompt, nullptr);
    if (raw_prompt == nullptr) {
        return env->NewStringUTF("Error: Failed to read prompt");
    }

    const std::string prompt_text(raw_prompt);
    env->ReleaseStringUTFChars(prompt, raw_prompt);

    const llama_vocab * vocab = llama_model_get_vocab(g_model);
    std::vector<llama_token> prompt_tokens = tokenize_prompt(vocab, prompt_text);
    if (prompt_tokens.empty()) {
        return env->NewStringUTF("Error: Prompt tokenization failed");
    }

    llama_memory_clear(llama_get_memory(g_ctx), false);

    llama_batch prompt_batch = llama_batch_get_one(prompt_tokens.data(), static_cast<int32_t>(prompt_tokens.size()));
    const int decode_result = llama_decode(g_ctx, prompt_batch);
    if (decode_result < 0) {
        LOGE("Prompt decode failed: %d", decode_result);
        return env->NewStringUTF("Error: Prompt decode failed");
    }

    jclass callback_class = nullptr;
    jmethodID callback_method = nullptr;
    if (on_token_received != nullptr) {
        callback_class = env->GetObjectClass(on_token_received);
        if (callback_class != nullptr) {
            callback_method = env->GetMethodID(callback_class, "invoke", "(Ljava/lang/Object;)Ljava/lang/Object;");
        }
    }

    const int token_limit = max_tokens > 0 ? max_tokens : DEFAULT_MAX_TOKENS;
    const float temp = temperature > 0.0f ? temperature : DEFAULT_TEMPERATURE;
    llama_sampler * sampler = llama_sampler_init_temp(temp);

    LOGI("Starting generation with max_tokens=%d, temperature=%.2f", token_limit, temp);

    std::string response;
    response.reserve(static_cast<size_t>(token_limit) * 4);

    for (int i = 0; i < token_limit; ++i) {
        const llama_token next_token = llama_sampler_sample(sampler, g_ctx, 0);
        if (llama_token_is_eog(vocab, next_token)) {
            LOGI("End of generation reached at token %d", i);
            break;
        }

        char piece[256];
        const int piece_size = llama_token_to_piece(vocab, next_token, piece, sizeof(piece), 0, true);
        if (piece_size > 0) {
            response.append(piece, static_cast<size_t>(piece_size));

            if (callback_method != nullptr) {
                jstring token_text = env->NewStringUTF(std::string(piece, static_cast<size_t>(piece_size)).c_str());
                env->CallObjectMethod(on_token_received, callback_method, token_text);
                env->DeleteLocalRef(token_text);
            }
        }

        llama_token current = next_token;
        llama_batch next_batch = llama_batch_get_one(&current, 1);
        const int next_decode_result = llama_decode(g_ctx, next_batch);
        if (next_decode_result < 0) {
            LOGE("Token decode failed: %d", next_decode_result);
            break;
        }
    }

    llama_sampler_free(sampler);

    if (callback_class != nullptr) {
        env->DeleteLocalRef(callback_class);
    }

    return env->NewStringUTF(response.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_aethernovax_droidlocalai_engine_LlamaEngine_unloadModel(JNIEnv * /* env */, jobject /* thiz */) {
    std::lock_guard<std::mutex> lock(g_mutex);
    unload_model_locked();
}
