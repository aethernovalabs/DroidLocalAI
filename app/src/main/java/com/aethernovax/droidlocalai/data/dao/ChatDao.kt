package com.aethernovax.droidlocalai.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aethernovax.droidlocalai.data.entities.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getChatsByProject(projectId: Long): Flow<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)

    @Query("DELETE FROM chats WHERE projectId = :projectId")
    suspend fun deleteChatsByProject(projectId: Long)
}
