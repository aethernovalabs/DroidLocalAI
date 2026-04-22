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
                    selected = currentRoute == "image",
                    onClick = { navController.navigate("image") },
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chats") },
                    label = { Text("Chats") },
                    selected = currentRoute?.startsWith("chats") == true || currentRoute?.startsWith("chat_room") == true,
                    onClick = { navController.navigate("chats") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Folder, contentDescription = "Project") },
                    label = { Text("Project") },
                    selected = currentRoute?.startsWith("projects") == true || currentRoute?.startsWith("project_detail") == true,
                    onClick = { navController.navigate("projects") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Memory, contentDescription = "Models") },
                    label = { Text("Models") },
                    selected = currentRoute == "models",
                    onClick = { navController.navigate("models") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Setting") },
                    label = { Text("Setting") },
                    selected = currentRoute == "settings",
                    onClick = { navController.navigate("settings") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = "chats",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("image") { ModelListScreen(navController) }
            composable("chats") { ChatListScreen(navController, chatManager) }
            composable("projects") { ProjectListScreen(navController) }
            composable("models") { ModelsScreen() }
            composable("settings") { SettingsScreen(navController) }
            composable("llm_settings") { LlmSettingsScreen(navController) }

            composable(
                route = "project_detail/{projectId}",
                arguments = listOf(navArgument("projectId") { type = NavType.LongType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
                ProjectDetailScreen(projectId = projectId, navController = navController)
            }

            composable(
                route = "chat_room/{chatId}",
                arguments = listOf(navArgument("chatId") { type = NavType.LongType })
            ) { backStackEntry ->
                val chatId = backStackEntry.arguments?.getLong("chatId") ?: 0L
                ChatRoomScreen(chatId = chatId, navController = navController)
            }
        }
    }
}
