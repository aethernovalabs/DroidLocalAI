package com.aethernovax.droidlocalai.engine

import android.util.Log

class LlamaEngine {
    companion object {
        private const val TAG = "LlamaEngine"
        var isLibraryLoaded = false
            private set
        
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
    fun loadModelSafe(modelPath: String): Boolean {
        if (!isLibraryLoaded) return false
        return try {
            loadModel(modelPath)
        } catch (e: UnsatisfiedLinkError) {
            false
        }
    }

    private external fun loadModel(modelPath: String): Boolean

    /**
     * Generate a response based on the prompt.
     */
    fun generateResponseSafe(prompt: String, onTokenReceived: (String) -> Unit): String {
        if (!isLibraryLoaded) return "Error: Library not loaded"
        return try {
            generateResponse(prompt, onTokenReceived)
        } catch (e: UnsatisfiedLinkError) {
            "Error: Native method not found"
        }
    }

    private external fun generateResponse(
        prompt: String, 
        onTokenReceived: (String) -> Unit
    ): String

    /**
     * Free resources.
     */
    fun unloadModelSafe() {
        if (isLibraryLoaded) {
            try {
                unloadModel()
            } catch (e: UnsatisfiedLinkError) {
                // Ignore
            }
        }
    }

    private external fun unloadModel()
}
