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

// AppNavigation.kt
@Composable
fun MainAppScreen(chatManager: ChatManager) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.Black) {
                // 1. Image
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Image, contentDescription = "image") },
                    label = { Text("image") },
                    selected = currentRoute == "image",
                    onClick = { navController.navigate("image") }
                )
                // 2. Chats
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chats") },
                    label = { Text("Chats") },
                    selected = currentRoute == "chats",
                    onClick = { navController.navigate("chats") }
                )
                // 3. Project
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Folder, contentDescription = "Project") },
                    label = { Text("Project") },
                    selected = currentRoute == "projects",
                    onClick = { navController.navigate("projects") }
                )
                // 4. Models
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Memory, contentDescription = "Models") },
                    label = { Text("Models") },
                    selected = currentRoute == "models",
                    onClick = { navController.navigate("models") }
                )
                // 5. Setting
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
            composable("chats") { ChatListScreen(chatManager) }
            composable("projects") { ProjectListScreen(navController) }
            composable("models") { ModelsScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}