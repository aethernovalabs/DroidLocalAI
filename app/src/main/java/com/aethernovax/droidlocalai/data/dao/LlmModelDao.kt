package com.aethernovax.droidlocalai.data.dao

import androidx.room.*
import com.aethernovax.droidlocalai.data.entities.LlmModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LlmModelDao {
    @Query("SELECT * FROM llm_models ORDER BY id DESC")
    fun getAllModels(): Flow<List<LlmModelEntity>>

    @Query("SELECT * FROM llm_models WHERE isSelected = 1 LIMIT 1")
    suspend fun getSelectedModel(): LlmModelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: LlmModelEntity): Long

    @Query("UPDATE llm_models SET isSelected = 0")
    suspend fun deselectAll()

    @Query("UPDATE llm_models SET isSelected = 1 WHERE id = :id")
    suspend fun selectModel(id: Long)

    @Delete
    suspend fun deleteModel(model: LlmModelEntity)
}
