package com.aethernovax.droidlocalai.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
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
import com.aethernovax.droidlocalai.data.entities.LorebookKeywordEntity
import com.aethernovax.droidlocalai.data.entities.ProjectEntity
import com.aethernovax.droidlocalai.viewmodel.ChatViewModel
import com.aethernovax.droidlocalai.viewmodel.ProjectsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    projectId: Long,
    navController: NavController,
    projectsViewModel: ProjectsViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel()
) {
    val project by projectsViewModel.getProjectById(projectId).collectAsState(initial = null)
    val keywords by projectsViewModel.getKeywords(projectId).collectAsState(initial = emptyList())
    val history by chatViewModel.getSessionsForProject(projectId).collectAsState(initial = emptyList())

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAddRagDialog by remember { mutableStateOf(false) }
    var showAddKeywordDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog && project != null) {
        EditProjectDialog(
            project = project!!,
            onDismiss = { showSettingsDialog = false },
            onConfirm = { updatedProject ->
                projectsViewModel.updateProject(updatedProject)
                showSettingsDialog = false
            }
        )
    }

    if (showAddRagDialog && project != null) {
        AddRagDialog(
            currentRag = project!!.lorebookRag,
            onDismiss = { showAddRagDialog = false },
            onConfirm = { newRag ->
                projectsViewModel.updateProject(project!!.copy(lorebookRag = newRag))
                showAddRagDialog = false
            }
        )
    }

    if (showAddKeywordDialog) {
        AddKeywordDialog(
            onDismiss = { showAddKeywordDialog = false },
            onConfirm = { keyword, desc ->
                projectsViewModel.addKeyword(projectId, keyword, desc)
                showAddKeywordDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(project?.name ?: "Project Detail", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Header
            item {
                Text(project?.name ?: "", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(project?.description ?: "", color = Color.Gray, fontSize = 14.sp)
            }

            // 1. Lorebook RAG
            item {
                SectionHeader("Lorebook RAG (Knowledge Base)", onAdd = { showAddRagDialog = true })
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        if (project?.lorebookRag.isNullOrBlank()) {
                            Text("No RAG data.", color = Color.Gray)
                        } else {
                            Column {
                                Text(project!!.lorebookRag, color = Color.White)
                                TextButton(
                                    onClick = { projectsViewModel.updateProject(project!!.copy(lorebookRag = "")) },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                                ) {
                                    Text("Hapus RAG")
                                }
                            }
                        }
                    }
                }
            }

            // 2. Lorebook Keywords
            item {
                SectionHeader("Lorebook Keywords", onAdd = { showAddKeywordDialog = true })
            }
            items(keywords) { kw ->
                KeywordItem(kw, onDelete = { projectsViewModel.deleteKeyword(kw) })
            }

            // 3. New Chat & History
            item {
                Button(
                    onClick = {
                        chatViewModel.createNewChat(projectId) { chatId ->
                            navController.navigate("chat_room/$chatId")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DD0E1)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start New Chat", color = Color.Black)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Chat History", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            items(history) { session ->
                ChatHistoryItem(
                    title = session.title,
                    onOpen = { navController.navigate("chat_room/${session.id}") },
                    onDelete = { chatViewModel.deleteSession(session) }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        IconButton(onClick = onAdd) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color(0xFF4DD0E1))
        }
    }
}

@Composable
fun KeywordItem(kw: LorebookKeywordEntity, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(kw.keyword, color = Color(0xFF4DD0E1), fontWeight = FontWeight.Bold)
                Text(kw.description, color = Color.Gray, fontSize = 12.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ChatHistoryItem(title: String, onOpen: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onOpen() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = Color.White, modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun EditProjectDialog(project: ProjectEntity, onDismiss: () -> Unit, onConfirm: (ProjectEntity) -> Unit) {
    var name by remember { mutableStateOf(project.name) }
    var desc by remember { mutableStateOf(project.description) }
    var prompt by remember { mutableStateOf(project.systemPrompt) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Project") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
                OutlinedTextField(value = prompt, onValueChange = { prompt = it }, label = { Text("System Prompt") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(project.copy(name = name, description = desc, systemPrompt = prompt)) }) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddRagDialog(currentRag: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var rag by remember { mutableStateOf(currentRag) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lorebook RAG") },
        text = {
            OutlinedTextField(
                value = rag,
                onValueChange = { rag = it },
                label = { Text("Deskripsi Panjang (Context)") },
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(rag) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddKeywordDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var keyword by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Keyword Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = keyword, onValueChange = { keyword = it }, label = { Text("Keyword") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(keyword, desc) }, enabled = keyword.isNotBlank()) {
                Text("Add")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
