package com.aethernovax.droidlocalai.data.dao

import androidx.room.*
import com.aethernovax.droidlocalai.data.entities.ChatSessionEntity
import com.aethernovax.droidlocalai.data.entities.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    // Sessions
    @Query("SELECT * FROM chat_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getSessionsByProject(projectId: Long): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE projectId = :projectId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastSessionByProject(projectId: Long): ChatSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity): Long

    @Delete
    suspend fun deleteSession(session: ChatSessionEntity)

    // Messages
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesByChat(chatId: Long): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long
}
