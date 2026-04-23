package com.aethernovax.droidlocalai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aethernovax.droidlocalai.data.AppDatabase
import com.aethernovax.droidlocalai.data.GenerationPreferences
import com.aethernovax.droidlocalai.data.entities.ChatSessionEntity
import com.aethernovax.droidlocalai.data.entities.MessageEntity
import com.aethernovax.droidlocalai.engine.LlamaEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val chatDao = database.chatDao()
    private val projectDao = database.projectDao()
    private val llmModelDao = database.llmModelDao()
    private val llamaEngine = LlamaEngine()
    private val generationPreferences = GenerationPreferences(application)

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
            // 1. Save user message
            chatDao.insertMessage(
                MessageEntity(chatId = chatId, role = "user", content = content)
            )

            // 2. Ensure Model is Loaded
            if (LlamaEngine.isLibraryLoaded) {
                val selectedModel = llmModelDao.getSelectedModel()
                if (selectedModel != null) {
                    val llmSettings = generationPreferences.llmSettings.first()
                    val loadSuccess = llamaEngine.loadModelSafe(
                        modelPath = selectedModel.path,
                        contextSize = llmSettings.contextSize
                    )
                    if (!loadSuccess) {
                        chatDao.insertMessage(
                            MessageEntity(
                                chatId = chatId,
                                role = "assistant",
                                content = "Error: gagal memuat model chat. Pastikan file GGUF valid dan library native berhasil dibuild."
                            )
                        )
                        return@launch
                    }
                } else {
                    chatDao.insertMessage(
                        MessageEntity(
                            chatId = chatId,
                            role = "assistant",
                            content = "Error: belum ada model chat yang dipilih."
                        )
                    )
                    return@launch
                }
            }

            // 3. Build Context (System Prompt + RAG + Keywords)
            val sessions = allSessions.first()
            val session = sessions.find { it.id == chatId } ?: return@launch
            val project = projectDao.getProjectById(session.projectId) ?: return@launch
            
            val rags = projectDao.getRagsByProject(project.id).first()
            val keywords = projectDao.getKeywordsByProject(project.id).first()

            val fullPrompt = buildString {
                append("System: ${project.systemPrompt}\n\n")
                
                if (rags.isNotEmpty()) {
                    append("Knowledge Base (RAG):\n")
                    rags.forEach { append("- ${it.title}: ${it.content}\n") }
                    append("\n")
                }

                val matchedKeywords = keywords.filter { 
                    content.contains(it.keyword, ignoreCase = true) 
                }
                if (matchedKeywords.isNotEmpty()) {
                    append("Relevant Context:\n")
                    matchedKeywords.forEach { append("- ${it.keyword}: ${it.description}\n") }
                    append("\n")
                }

                append("User: $content\n")
                append("Assistant: ")
            }

            // 4. Call Llama Engine
            val aiResponse = if (LlamaEngine.isLibraryLoaded) {
                val llmSettings = generationPreferences.llmSettings.first()
                llamaEngine.generateResponseSafe(
                    prompt = fullPrompt,
                    maxTokens = llmSettings.maxTokens,
                    temperature = llmSettings.temperature
                ) { token ->
                    // TODO: Update UI in real-time
                }
            } else {
                "Error: AI Engine (llama.cpp) library failed to load. Please check your NDK build."
            }

            // 5. Save AI response
            chatDao.insertMessage(
                MessageEntity(chatId = chatId, role = "assistant", content = aiResponse)
            )
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        llamaEngine.unloadModelSafe()
    }
}
