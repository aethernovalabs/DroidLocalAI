#include <jni.h>
#include <string>
#include <android/log.h>

#define TAG "LlamaAndroid"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

// Placeholder for llama.cpp headers
// #include "llama.h"

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_aethernovax_droidlocalai_engine_LlamaEngine_loadModel(JNIEnv *env, jobject thiz, jstring model_path) {
    const char *path = env->GetStringUTFChars(model_path, nullptr);
    LOGI("Loading model from: %s", path);

    // TODO: Integrate llama_load_model_from_file

    env->ReleaseStringUTFChars(model_path, path);
    return JNI_TRUE;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_aethernovax_droidlocalai_engine_LlamaEngine_generateResponse(JNIEnv *env, jobject thiz, jstring prompt, jobject on_token_received) {
    const char *c_prompt = env->GetStringUTFChars(prompt, nullptr);
    LOGI("Generating response for prompt: %s", c_prompt);

    // TODO: Integrate llama_decode / sampling
    std::string response = "AI Response Placeholder for: ";
    response += c_prompt;

    // Simulate token callback
    jclass callback_class = env->GetObjectClass(on_token_received);
    jmethodID method_id = env->GetMethodID(callback_class, "invoke", "(Ljava/lang/Object;)Ljava/lang/Object;");

    // For each token:
    // jstring token = env->NewStringUTF("word ");
    // env->CallObjectMethod(on_token_received, method_id, token);
    // env->DeleteLocalRef(token);

    env->ReleaseStringUTFChars(prompt, c_prompt);
    return env->NewStringUTF(response.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_aethernovax_droidlocalai_engine_LlamaEngine_unloadModel(JNIEnv *env, jobject thiz) {
    LOGI("Unloading model");
    // TODO: llama_free
}
