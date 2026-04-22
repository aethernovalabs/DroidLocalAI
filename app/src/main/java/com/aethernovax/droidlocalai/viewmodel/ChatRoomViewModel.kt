package com.aethernovax.droidlocalai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aethernovax.droidlocalai.data.AppDatabase
import com.aethernovax.droidlocalai.data.entities.MessageEntity
import com.aethernovax.droidlocalai.data.entities.ProjectEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatRoomViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val chatDao = database.chatDao()
    private val projectDao = database.projectDao()

    private val _project = MutableStateFlow<ProjectEntity?>(null)
    val project: StateFlow<ProjectEntity?> = _project.asStateFlow()

    fun loadProject(projectId: Long) {
        viewModelScope.launch {
            _project.value = projectDao.getProjectById(projectId)
        }
    }

    fun getMessages(chatId: Long): Flow<List<MessageEntity>> {
        return chatDao.getMessagesByChat(chatId)
    }

    fun sendMessage(chatId: Long, message: String) {
        if (message.isBlank()) return
        
        viewModelScope.launch {
            // Save user message
            chatDao.insertMessage(
                MessageEntity(
                    chatId = chatId,
                    role = "user",
                    content = message
                )
            )

            // Placeholder for AI response logic
            // In Phase 4, we will connect this to llama.cpp
            simulateAiResponse(chatId, "Echo: $message")
        }
    }

    private fun simulateAiResponse(chatId: Long, response: String) {
        viewModelScope.launch {
            chatDao.insertMessage(
                MessageEntity(
                    chatId = chatId,
                    role = "assistant",
                    content = response
                )
            )
        }
    }
}
