package com.aethernovax.droidlocalai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aethernovax.droidlocalai.data.entities.ProjectEntity
import com.aethernovax.droidlocalai.viewmodel.ChatViewModel
import com.aethernovax.droidlocalai.viewmodel.ProjectsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    chatManager: ChatManager,
    chatViewModel: ChatViewModel = viewModel(),
    projectsViewModel: ProjectsViewModel = viewModel()
) {
    val sessions by chatViewModel.allSessions.collectAsState(initial = emptyList())
    val projects by projectsViewModel.allProjects.collectAsState(initial = emptyList())
    var showNewChatDialog by remember { mutableStateOf(false) }

    if (showNewChatDialog) {
        SelectProjectDialog(
            projects = projects,
            onDismiss = { showNewChatDialog = false },
            onProjectSelected = { projectId ->
                chatViewModel.createNewChat(projectId) { chatId ->
                    navController.navigate("chat_room/$chatId")
                }
                showNewChatDialog = false
            }
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF121212))) {
        TopAppBar(
            title = { Text("Chats", color = Color.White, fontWeight = FontWeight.Bold) },
            actions = {
                OutlinedButton(onClick = { showNewChatDialog = true }) {
                    Text("+ New Chat", color = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )
        
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sessions) { session ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("chat_room/${session.id}") },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(session.title, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Session ID: ${session.id}", color = Color.Gray, fontSize = 12.sp)
                        }
                        IconButton(onClick = { chatViewModel.deleteSession(session) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                        }
                    }
                }
            }

            if (sessions.isEmpty()) {
                item {
                    Text(
                        "No chat history. Start a new chat!",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SelectProjectDialog(
    projects: List<ProjectEntity>,
    onDismiss: () -> Unit,
    onProjectSelected: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Project for New Chat") },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                items(projects) { project ->
                    ListItem(
                        headlineContent = { Text(project.name) },
                        supportingContent = { Text(project.description) },
                        modifier = Modifier.clickable { onProjectSelected(project.id) }
                    )
                }
                if (projects.isEmpty()) {
                    item { Text("No projects available. Please create a project first.") }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
