package com.aethernovax.droidlocalai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aethernovax.droidlocalai.data.AppDatabase
import com.aethernovax.droidlocalai.data.entities.ChatSessionEntity
import com.aethernovax.droidlocalai.data.entities.MessageEntity
import com.aethernovax.droidlocalai.data.entities.ProjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val chatDao = database.chatDao()
    private val projectDao = database.projectDao()

    val allSessions: Flow<List<ChatSessionEntity>> = chatDao.getAllSessions()

    fun getSessionsForProject(projectId: Long): Flow<List<ChatSessionEntity>> {
        return chatDao.getSessionsByProject(projectId)
    }

    fun startChatFromProject(projectId: Long, onNavigate: (Long) -> Unit) {
        viewModelScope.launch {
            val lastSession = chatDao.getLastSessionByProject(projectId)
            if (lastSession != null) {
                onNavigate(lastSession.id)
            } else {
                val project = projectDao.getProjectById(projectId)
                val newSessionId = chatDao.insertSession(
                    ChatSessionEntity(
                        projectId = projectId,
                        title = "Chat with ${project?.name ?: "Assistant"}"
                    )
                )
                onNavigate(newSessionId)
            }
        }
    }

    fun createNewChat(projectId: Long, onNavigate: (Long) -> Unit) {
        viewModelScope.launch {
            val project = projectDao.getProjectById(projectId)
            val newSessionId = chatDao.insertSession(
                ChatSessionEntity(
                    projectId = projectId,
                    title = "New Chat: ${project?.name ?: "Assistant"}"
                )
            )
            onNavigate(newSessionId)
        }
    }

    fun deleteSession(session: ChatSessionEntity) {
        viewModelScope.launch {
            chatDao.deleteSession(session)
        }
    }

    fun getMessages(chatId: Long): Flow<List<MessageEntity>> {
        return chatDao.getMessagesByChat(chatId)
    }

    fun sendMessage(chatId: Long, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            chatDao.insertMessage(
                MessageEntity(chatId = chatId, role = "user", content = content)
            )
            // Simulating AI
            chatDao.insertMessage(
                MessageEntity(chatId = chatId, role = "assistant", content = "Echo: $content")
            )
        }
    }
}
