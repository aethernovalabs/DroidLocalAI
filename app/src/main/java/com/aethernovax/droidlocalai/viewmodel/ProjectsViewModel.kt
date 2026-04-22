package com.aethernovax.droidlocalai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aethernovax.droidlocalai.data.AppDatabase
import com.aethernovax.droidlocalai.data.entities.ProjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProjectsViewModel(application: Application) : AndroidViewModel(application) {
    private val projectDao = AppDatabase.getDatabase(application).projectDao()
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun addProject(name: String, description: String, systemPrompt: String, lorebook: String) {
        viewModelScope.launch {
            val newProject = ProjectEntity(
                name = name,
                description = description,
                systemPrompt = systemPrompt,
                lorebook = lorebook
            )
            projectDao.insertProject(newProject)
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            projectDao.deleteProject(project)
        }
    }
}
