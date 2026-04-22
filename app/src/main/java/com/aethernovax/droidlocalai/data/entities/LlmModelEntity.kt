package com.aethernovax.droidlocalai.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "llm_models")
data class LlmModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val path: String,
    val size: String,
    val type: String = "GGUF",
    val isSelected: Boolean = false
)
