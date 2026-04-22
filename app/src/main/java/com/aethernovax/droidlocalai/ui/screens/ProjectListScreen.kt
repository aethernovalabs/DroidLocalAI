package com.aethernovax.droidlocalai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
import com.aethernovax.droidlocalai.viewmodel.ProjectsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    navController: NavController,
    projectsViewModel: ProjectsViewModel = viewModel()
) {
    val projects by projectsViewModel.allProjects.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddProjectDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, desc, prompt, lore ->
                projectsViewModel.addProject(name, desc, prompt, lore)
                showDialog = false
            }
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF121212))) {
        TopAppBar(
            title = { Text("Projects", color = Color.White) },
            actions = {
                OutlinedButton(onClick = { showDialog = true }) {
                    Text("+ New", color = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )

        Text(
            "Projects group related chats with shared context and instructions.",
            color = Color.Gray,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(projects) { project ->
                ProjectItem(project) {
                    // Navigasi ke ChatRoom (Akan diimplementasikan di Fase 3)
                    navController.navigate("chat_room/${project.id}")
                }
            }
            
            if (projects.isEmpty()) {
                item {
                    Text(
                        "No projects found. Create one to get started!",
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
fun ProjectItem(project: ProjectEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF4DD0E1)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        project.name.take(1).uppercase(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)) {
                Text(
                    project.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    project.description,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.White)
        }
    }
}

@Composable
fun AddProjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    var lorebook by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Project") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = systemPrompt,
                    onValueChange = { systemPrompt = it },
                    label = { Text("System Prompt") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lorebook,
                    onValueChange = { lorebook = it },
                    label = { Text("Lorebook (Context)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, description, systemPrompt, lorebook) },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
