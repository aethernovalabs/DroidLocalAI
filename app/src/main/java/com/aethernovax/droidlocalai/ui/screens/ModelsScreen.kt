package com.aethernovax.droidlocalai.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aethernovax.droidlocalai.viewmodel.ModelsViewModel
import com.aethernovax.droidlocalai.viewmodel.ModelInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelsScreen(modelsViewModel: ModelsViewModel = viewModel()) {
    val context = LocalContext.current
    val models by modelsViewModel.models.collectAsState()

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

        Text("Text Models", color = Color.White, modifier = Modifier.padding(start = 16.dp))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color(0xFF4DD0E1)
        )

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(models) { model: ModelInfo ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = model.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    null,
                                    tint = Color(0xFF4DD0E1),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "installed",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                                )
                                IconButton(
                                    onClick = { modelsViewModel.removeModel(model) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus Model",
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            ModelTag(model.size)
                            ModelTag(model.type)
                        }
                    }
                }
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
