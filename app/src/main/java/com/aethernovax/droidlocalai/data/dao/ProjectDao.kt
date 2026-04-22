package com.aethernovax.droidlocalai.data.dao

import androidx.room.*
import com.aethernovax.droidlocalai.data.entities.ProjectEntity
import com.aethernovax.droidlocalai.data.entities.LorebookKeywordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    // Keywords
    @Query("SELECT * FROM lorebook_keywords WHERE projectId = :projectId")
    fun getKeywordsByProject(projectId: Long): Flow<List<LorebookKeywordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword: LorebookKeywordEntity)

    @Delete
    suspend fun deleteKeyword(keyword: LorebookKeywordEntity)
}
