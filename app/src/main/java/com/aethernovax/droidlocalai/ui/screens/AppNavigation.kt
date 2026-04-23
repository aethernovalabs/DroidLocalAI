package com.aethernovax.droidlocalai.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aethernovax.droidlocalai.navigation.Screen

@Composable
fun MainAppScreen(chatManager: ChatManager) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.Black) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Image, contentDescription = "image") },
                    label = { Text("image") },
                    selected = currentRoute == Screen.ImageModelList.route,
                    onClick = { navController.navigate(Screen.ImageModelList.route) },
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chats") },
                    label = { Text("Chats") },
                    selected = currentRoute == Screen.TextAiChats.route || currentRoute == Screen.TextAiChatRoom.route,
                    onClick = { navController.navigate(Screen.TextAiChats.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Folder, contentDescription = "Project") },
                    label = { Text("Project") },
                    selected = currentRoute == Screen.TextAiProjects.route || currentRoute == Screen.TextAiProjectDetail.route,
                    onClick = { navController.navigate(Screen.TextAiProjects.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Memory, contentDescription = "Models") },
                    label = { Text("Models") },
                    selected = currentRoute == Screen.TextAiLocalModels.route,
                    onClick = { navController.navigate(Screen.TextAiLocalModels.route) }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Setting") },
                    label = { Text("Setting") },
                    selected = currentRoute == Screen.TextAiSettings.route,
                    onClick = { navController.navigate(Screen.TextAiSettings.route) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.TextAiChats.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.ImageModelList.route) { ImageModelListScreen(navController) }
            composable(Screen.TextAiChats.route) { ChatListScreen(navController, chatManager) }
            composable(Screen.TextAiProjects.route) { TextModelProjectListScreen(navController) }
            composable(Screen.TextAiLocalModels.route) { TextModelLocalModelsScreen() }
            composable(Screen.TextAiSettings.route) { TextModelSettingsScreen(navController) }
            composable(Screen.TextAiLlmSettings.route) { TextModelLlmSettingsScreen(navController) }

            composable(
                route = Screen.ImageModelRun.route,
                arguments = listOf(
                    navArgument("modelId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val modelId = backStackEntry.arguments?.getString("modelId") ?: ""

                ImageModelRunScreen(
                    modelId = modelId,
                    navController = navController
                )
            }
            composable(Screen.ImageModelUpscale.route) {
                ImageModelUpscaleScreen(navController)
            }

            composable(
                route = Screen.TextAiProjectDetail.route,
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
                TextModelProjectDetailScreen(projectId = projectId, navController = navController)
            }

            composable(
                route = Screen.TextAiChatRoom.route,
                arguments = listOf(navArgument("chatId") { type = NavType.LongType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getLong("chatId") ?: 0L
                ChatRoomScreen(chatId = chatId, navController = navController)
            }
        }
    }
}
