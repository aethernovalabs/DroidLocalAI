package com.aethernovax.droidlocalai.engine

import android.util.Log
import java.io.File

class LlamaEngine {
    companion object {
        private const val TAG = "LlamaEngine"
        var isLibraryLoaded = false
            private set
        private var loadedModelPath: String? = null
        private var loadedContextSize: Int? = null
        
        init {
            try {
                System.loadLibrary("llama-android")
                isLibraryLoaded = true
            } catch (e: Throwable) {
                Log.e(TAG, "CRITICAL: Failed to load llama-android library. Ensure NDK build is successful.", e)
            }
        }
    }

    /**
     * Load a GGUF model from the given path.
     */
    fun loadModelSafe(modelPath: String, contextSize: Int = 2048): Boolean {
        if (!isLibraryLoaded) return false
        if (modelPath.isBlank()) return false

        val modelFile = File(modelPath)
        if (!modelFile.exists() || !modelFile.isFile) {
            Log.e(TAG, "Model file not found: $modelPath")
            return false
        }

        if (loadedModelPath == modelPath && loadedContextSize == contextSize) {
            return true
        }

        return try {
            loadedModelPath = null
            loadedContextSize = null
            val loaded = loadModel(modelPath, contextSize)
            if (loaded) {
                loadedModelPath = modelPath
                loadedContextSize = contextSize
            }
            loaded
        } catch (e: UnsatisfiedLinkError) {
            false
        }
    }

    private external fun loadModel(modelPath: String, contextSize: Int): Boolean

    /**
     * Generate a response based on the prompt.
     */
    fun generateResponseSafe(
        prompt: String,
        maxTokens: Int = 512,
        temperature: Float = 0.7f,
        onTokenReceived: (String) -> Unit
    ): String {
        if (!isLibraryLoaded) return "Error: Library not loaded"
        return try {
            generateResponse(prompt, maxTokens, temperature, onTokenReceived)
        } catch (e: UnsatisfiedLinkError) {
            "Error: Native method not found"
        }
    }

    private external fun generateResponse(
        prompt: String, 
        maxTokens: Int,
        temperature: Float,
        onTokenReceived: (String) -> Unit
    ): String

    /**
     * Free resources.
     */
    fun unloadModelSafe() {
        if (isLibraryLoaded) {
            try {
                unloadModel()
                loadedModelPath = null
                loadedContextSize = null
            } catch (e: UnsatisfiedLinkError) {
                // Ignore
            }
        }
    }

    private external fun unloadModel()
}
