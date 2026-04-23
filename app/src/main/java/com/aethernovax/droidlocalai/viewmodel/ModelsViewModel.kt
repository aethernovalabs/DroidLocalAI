package com.aethernovax.droidlocalai.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aethernovax.droidlocalai.data.AppDatabase
import com.aethernovax.droidlocalai.data.entities.LlmModelEntity
import com.aethernovax.droidlocalai.engine.LlamaEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.DecimalFormat

class ModelsViewModel(application: Application) : AndroidViewModel(application) {
    private val llmModelDao = AppDatabase.getDatabase(application).llmModelDao()
    private val llamaEngine = LlamaEngine()
    
    val models: Flow<List<LlmModelEntity>> = llmModelDao.getAllModels()

    fun addModel(context: Context, uri: Uri) {
        val fileName = getFileName(context, uri) ?: "Unknown Model"
        
        if (!fileName.lowercase().endsWith(".gguf")) {
            Toast.makeText(context, "Hanya file .gguf yang diperbolehkan!", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            val storedFile = copyModelToInternalStorage(context, uri, fileName)
            if (storedFile == null) {
                Toast.makeText(context, "Gagal mengimpor model GGUF.", Toast.LENGTH_LONG).show()
                return@launch
            }

            val newModel = LlmModelEntity(
                name = storedFile.name,
                path = storedFile.absolutePath,
                size = formatFileSize(storedFile.length())
            )
            llmModelDao.insertModel(newModel)
            Toast.makeText(context, "Model ${storedFile.name} berhasil diimpor.", Toast.LENGTH_SHORT).show()
        }
    }

    fun selectAndLoadModel(context: Context, model: LlmModelEntity) {
        viewModelScope.launch {
            // 1. Update selection in DB
            llmModelDao.deselectAll()
            llmModelDao.selectModel(model.id)
            
            // 2. Try to load it into the engine
            val success = llamaEngine.loadModelSafe(model.path)
            if (success) {
                Toast.makeText(context, "Model ${model.name} loaded successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to load ${model.name}. Check NDK build and model file.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun removeModel(model: LlmModelEntity) {
        viewModelScope.launch {
            llmModelDao.deleteModel(model)
        }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) result = it.getString(index)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/') ?: -1
            if (cut != -1) result = result?.substring(cut + 1)
        }
        return result
    }

    private suspend fun copyModelToInternalStorage(context: Context, uri: Uri, fileName: String): File? {
        return withContext(Dispatchers.IO) {
            runCatching {
                val modelsDir = File(context.filesDir, "llm-models").apply {
                    if (!exists()) {
                        mkdirs()
                    }
                }

                val sanitizedName = fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")
                val targetFile = File(modelsDir, sanitizedName)

                context.contentResolver.openInputStream(uri)?.use { input ->
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: return@withContext null

                targetFile
            }.getOrNull()
        }
    }

    private fun getFileSize(context: Context, uri: Uri): String? {
        var size: Long = 0
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.SIZE)
                    if (index != -1) size = it.getLong(index)
                }
            }
        }
        if (size <= 0) return null

        return formatFileSize(size)
    }

    private fun formatFileSize(size: Long): String {
        val df = DecimalFormat("#.##")
        return when {
            size < 1024 -> "${size}B"
            size < 1024 * 1024 -> "${df.format(size / 1024.0)}KB"
            size < 1024 * 1024 * 1024 -> "${df.format(size / (1024.0 * 1024.0))}MB"
            else -> "${df.format(size / (1024.0 * 1024.0 * 1024.0))}GB"
        }
    }
}
