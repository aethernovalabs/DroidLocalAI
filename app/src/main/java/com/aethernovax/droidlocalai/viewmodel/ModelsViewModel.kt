package com.aethernovax.droidlocalai.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DecimalFormat

data class ModelInfo(
    val name: String,
    val path: String,
    val size: String = "Unknown",
    val type: String = "GGUF",
    val isInstalled: Boolean = true,
)

class ModelsViewModel : ViewModel() {
    private val _models = MutableStateFlow<List<ModelInfo>>(emptyList())
    val models: StateFlow<List<ModelInfo>> = _models.asStateFlow()

    fun addModel(context: Context, uri: Uri) {
        val fileName = getFileName(context, uri) ?: "Unknown Model"
        
        // Filter extension .gguf
        if (!fileName.lowercase().endsWith(".gguf")) {
            Toast.makeText(context, "Hanya file .gguf yang diperbolehkan!", Toast.LENGTH_SHORT).show()
            return
        }

        val fileSize = getFileSize(context, uri) ?: "Unknown Size"

        val newModel = ModelInfo(
            name = fileName,
            path = uri.toString(),
            size = fileSize
        )
        _models.value += newModel
    }

    fun removeModel(model: ModelInfo) {
        _models.value = _models.value.filter { it.path != model.path }
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

        val df = DecimalFormat("#.##")
        return when {
            size < 1024 -> "${size}B"
            size < (1024 * 1024) -> "${df.format(size / 1024.0)}KB"
            size < (1024 * 1024 * 1024) -> "${df.format(size / (1024.0 * 1024.0))}MB"
            else -> "${df.format(size / (1024.0 * 1024.0 * 1024.0))}GB"
        }
    }
}
