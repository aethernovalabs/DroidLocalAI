package com.aethernovax.droidlocalai.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
            NavigationBar {
                // Tombol 1: Generate Gambar
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Image, contentDescription = "Gambar") },
                    label = { Text("Gambar") },
                    selected = currentRoute?.startsWith("image") == true || currentRoute?.startsWith("model_run") == true || currentRoute == "upscale",
                    onClick = { 
                        navController.navigate(Screen.ModelList.route) { 
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        } 
                    }
                )
                // Tombol 2: Chat LLM
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat") },
                    label = { Text("Chat") },
                    selected = currentRoute == "chat",
                    onClick = { 
                        navController.navigate("chat") { 
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        } 
                    }
                )
                // Tombol 3: Lorebook
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Book, contentDescription = "Lorebook") },
                    label = { Text("Lorebook") },
                    selected = currentRoute == "lorebook",
                    onClick = { 
                        navController.navigate("lorebook") { 
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        } 
                    }
                )
                // Tombol 4: Pengaturan
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Pengaturan") },
                    label = { Text("Pengaturan") },
                    selected = currentRoute == "settings",
                    onClick = { 
                        navController.navigate("settings") { 
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        } 
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "chat",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Rute Fitur Gambar (DroidLocalAI)
            composable(Screen.ModelList.route) {
                ModelListScreen(navController)
            }
            composable(
                route = Screen.ModelRun.route,
                arguments = listOf(navArgument("modelId") { type = NavType.StringType })
            ) { backStackEntry ->
                val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
                ModelRunScreen(modelId = modelId, navController = navController)
            }
            composable(Screen.Upscale.route) {
                UpscaleScreen(navController)
            }

            // Rute Fitur Chat
            composable("chat") {
                ChatScreen(chatManager = chatManager)
            }

            // Rute Lorebook
            composable("lorebook") {
                LorebookScreen()
            }

            // Rute Pengaturan
            composable("settings") {
                SettingsScreen()
            }
        }
    }
}