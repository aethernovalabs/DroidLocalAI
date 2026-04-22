package com.aethernovax.droidlocalai.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val systemPrompt: String,
    val lorebook: String // Simple string for now, could be JSON
)
