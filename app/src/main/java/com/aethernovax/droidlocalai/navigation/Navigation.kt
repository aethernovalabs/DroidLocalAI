package com.aethernovax.droidlocalai.navigation

sealed class Screen(val route: String) {
    object ImageModelList : Screen("image_models")
    object ImageModelRun : Screen("image_model_run/{modelId}") {
        fun createRoute(modelId: String) = "image_model_run/$modelId"
    }
    object ImageModelUpscale : Screen("image_model_upscale")
    object TextAiChats : Screen("text_ai_chats")
    object TextAiChatRoom : Screen("text_ai_chat_room/{chatId}") {
        fun createRoute(chatId: Long) = "text_ai_chat_room/$chatId"
    }
    object TextAiProjects : Screen("text_ai_projects")
    object TextAiProjectDetail : Screen("text_ai_project_detail/{projectId}") {
        fun createRoute(projectId: Long) = "text_ai_project_detail/$projectId"
    }
    object TextAiLocalModels : Screen("text_ai_local_models")
    object TextAiSettings : Screen("text_ai_settings")
    object TextAiLlmSettings : Screen("text_ai_llm_settings")
}
