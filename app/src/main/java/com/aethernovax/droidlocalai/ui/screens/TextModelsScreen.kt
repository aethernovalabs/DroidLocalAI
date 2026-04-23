package com.aethernovax.droidlocalai.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aethernovax.droidlocalai.viewmodel.ModelsViewModel
import com.aethernovax.droidlocalai.data.entities.LlmModelEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextModelLocalModelsScreen(modelsViewModel: ModelsViewModel = viewModel()) {
    val context = LocalContext.current
    val models by modelsViewModel.models.collectAsState(initial = emptyList())

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            modelsViewModel.addModel(context, it)
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF121212))) {
        TopAppBar(
            title = { Text("Models LLM", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(50.dp)
                .border(
                    1.dp,
                    Color(0xFF4DD0E1), RoundedCornerShape(25.dp)
                )
                .clickable {
                    launcher.launch("*/*")
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Import Local File (GGUF)", color = Color.White)
        }

        Text("Imported Models", color = Color.White, modifier = Modifier.padding(start = 16.dp))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color(0xFF4DD0E1)
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(models) { model ->
                ModelItem(
                    model = model,
                    onLoad = { modelsViewModel.selectAndLoadModel(context, model) },
                    onUnload = { modelsViewModel.unloadCurrentModel() },
                    onDelete = { modelsViewModel.removeModel(model) }
                )
            }

            if (models.isEmpty()) {
                item {
                    Text(
                        "No models imported. Click the button above to add one.",
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 20.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ModelItem(model: LlmModelEntity, onLoad: () -> Unit, onUnload: () -> Unit, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (model.isSelected) Color(0xFF1E3A3A) else Color(0xFF222222)
        ),
        border = if (model.isSelected) borderStroke(1.dp, Color(0xFF4DD0E1)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ModelTag(model.size)
                        Spacer(modifier = Modifier.width(4.dp))
                        ModelTag(model.type)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (model.isSelected) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                tint = Color(0xFF4DD0E1),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                "Active",
                                color = Color(0xFF4DD0E1),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                        Button(
                            onClick = onUnload,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp).padding(end = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Unload", color = Color.White, fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = onLoad,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DD0E1))
                        ) {
                            Icon(Icons.Default.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Text("Load", color = Color.Black, fontSize = 12.sp)
                        }
                    }
                    
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp).padding(start = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModelTag(text: String) {
    Surface(
        color = Color(0xFF333333),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

private fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)
