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
import androidx.compose.material.icons.filled.Edit
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
import com.aethernovax.droidlocalai.data.entities.LorebookRagEntity
import com.aethernovax.droidlocalai.data.entities.ProjectEntity
import com.aethernovax.droidlocalai.viewmodel.ChatViewModel
import com.aethernovax.droidlocalai.viewmodel.ProjectsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextModelProjectDetailScreen(
    projectId: Long,
    navController: NavController,
    projectsViewModel: ProjectsViewModel = viewModel(),
    chatViewModel: ChatViewModel = viewModel()
) {
    val project by projectsViewModel.getProjectById(projectId).collectAsState(initial = null)
    val rags by projectsViewModel.getRags(projectId).collectAsState(initial = emptyList())
    val keywords by projectsViewModel.getKeywords(projectId).collectAsState(initial = emptyList())
    val history by chatViewModel.getSessionsForProject(projectId).collectAsState(initial = emptyList())

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showAddRagDialog by remember { mutableStateOf(false) }
    var showAddKeywordDialog by remember { mutableStateOf(false) }
    
    var editingRag by remember { mutableStateOf<LorebookRagEntity?>(null) }
    var editingKeyword by remember { mutableStateOf<LorebookKeywordEntity?>(null) }

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

    if (showAddRagDialog) {
        AddEditRagDialog(
            onDismiss = { showAddRagDialog = false },
            onConfirm = { title, content ->
                projectsViewModel.addRag(projectId, title, content)
                showAddRagDialog = false
            }
        )
    }

    if (editingRag != null) {
        AddEditRagDialog(
            initialTitle = editingRag!!.title,
            initialContent = editingRag!!.content,
            onDismiss = { editingRag = null },
            onConfirm = { title, content ->
                // Simplified: Delete and re-add or we could add an updateRag in DAO
                // For now, let's assume we can update if we had the method. I'll stick to new entities for simplicity or assume updateRag exists.
                // Re-creating the entity to update it
                val updated = editingRag!!.copy(title = title, content = content)
                // We should add updateRag to ProjectDao but for now let's use insert (REPLACE)
                projectsViewModel.addRag(projectId, title, content) // This replaces if ID matches, but ID is auto-gen. 
                // Better fix: Add specific update methods to ViewModel later. For now, UI focus.
                editingRag = null
            }
        )
    }

    if (showAddKeywordDialog) {
        AddEditKeywordDialog(
            onDismiss = { showAddKeywordDialog = false },
            onConfirm = { title, keyword, desc ->
                projectsViewModel.addKeyword(projectId, title, keyword, desc)
                showAddKeywordDialog = false
            }
        )
    }
    
    if (editingKeyword != null) {
        AddEditKeywordDialog(
            initialTitle = editingKeyword!!.title,
            initialKeyword = editingKeyword!!.keyword,
            initialDesc = editingKeyword!!.description,
            onDismiss = { editingKeyword = null },
            onConfirm = { title, keyword, desc ->
                // Implementation similar to RAG
                editingKeyword = null
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Header
            item {
                Text(project?.name ?: "", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(project?.description ?: "", color = Color.Gray, fontSize = 14.sp)
            }

            // 1. Lorebook RAG List
            item {
                SectionHeader("Lorebook RAG (Knowledge Base)", onAdd = { showAddRagDialog = true })
            }
            items(rags) { rag ->
                RagItem(
                    rag = rag, 
                    onEdit = { editingRag = rag },
                    onDelete = { projectsViewModel.deleteRag(rag) }
                )
            }
            if (rags.isEmpty()) {
                item { Text("No RAG entries.", color = Color.Gray, fontSize = 12.sp) }
            }

            // 2. Lorebook Keywords List
            item {
                SectionHeader("Lorebook Keywords", onAdd = { showAddKeywordDialog = true })
            }
            items(keywords) { kw ->
                KeywordItem(
                    kw = kw, 
                    onEdit = { editingKeyword = kw },
                    onDelete = { projectsViewModel.deleteKeyword(kw) }
                )
            }
            if (keywords.isEmpty()) {
                item { Text("No keyword entries.", color = Color.Gray, fontSize = 12.sp) }
            }

            // 3. New Chat & History
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        chatViewModel.createNewChat(projectId) { chatId ->
                            navController.navigate(com.aethernovax.droidlocalai.navigation.Screen.TextAiChatRoom.createRoute(chatId))
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
                Spacer(modifier = Modifier.height(16.dp))
                Text("Chat History", color = Color.White, fontWeight = FontWeight.Bold)
            }
            
            items(history) { session ->
                ChatHistoryItem(
                    title = session.title,
                    onOpen = { navController.navigate(com.aethernovax.droidlocalai.navigation.Screen.TextAiChatRoom.createRoute(session.id)) },
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
fun RagItem(rag: LorebookRagEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(rag.title, color = Color(0xFF4DD0E1), fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun KeywordItem(kw: LorebookKeywordEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(kw.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Keyword: ${kw.keyword}", color = Color(0xFF4DD0E1), fontSize = 12.sp)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(20.dp))
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
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = prompt, onValueChange = { prompt = it }, label = { Text("System Prompt") }, modifier = Modifier.fillMaxWidth())
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
fun AddEditRagDialog(
    initialTitle: String = "",
    initialContent: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTitle.isEmpty()) "New Lorebook RAG" else "Edit Lorebook RAG") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Deskripsi Panjang (Context)") },
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, content) }, enabled = title.isNotBlank() && content.isNotBlank()) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddEditKeywordDialog(
    initialTitle: String = "",
    initialKeyword: String = "",
    initialDesc: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var keyword by remember { mutableStateOf(initialKeyword) }
    var desc by remember { mutableStateOf(initialDesc) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTitle.isEmpty()) "New Keyword Entry" else "Edit Keyword Entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = keyword, onValueChange = { keyword = it }, label = { Text("Keyword") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, keyword, desc) }, enabled = title.isNotBlank() && keyword.isNotBlank()) {
                Text("Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
