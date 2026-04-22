package com.aethernovax.droidlocalai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aethernovax.droidlocalai.data.AppDatabase
import com.aethernovax.droidlocalai.data.entities.ProjectEntity
import com.aethernovax.droidlocalai.data.entities.LorebookKeywordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ProjectsViewModel(application: Application) : AndroidViewModel(application) {
    private val projectDao = AppDatabase.getDatabase(application).projectDao()
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun addProject(name: String, description: String) {
        viewModelScope.launch {
            val newProject = ProjectEntity(
                name = name,
                description = description
            )
            projectDao.insertProject(newProject)
        }
    }

    fun updateProject(project: ProjectEntity) {
        viewModelScope.launch {
            projectDao.updateProject(project)
        }
    }

    fun getProjectById(projectId: Long): Flow<ProjectEntity?> = flow {
        emit(projectDao.getProjectById(projectId))
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            projectDao.deleteProject(project)
        }
    }

    // Keywords
    fun getKeywords(projectId: Long): Flow<List<LorebookKeywordEntity>> {
        return projectDao.getKeywordsByProject(projectId)
    }

    fun addKeyword(projectId: Long, keyword: String, description: String) {
        viewModelScope.launch {
            projectDao.insertKeyword(
                LorebookKeywordEntity(projectId = projectId, keyword = keyword, description = description)
            )
        }
    }

    fun deleteKeyword(keyword: LorebookKeywordEntity) {
        viewModelScope.launch {
            projectDao.deleteKeyword(keyword)
        }
    }
}
