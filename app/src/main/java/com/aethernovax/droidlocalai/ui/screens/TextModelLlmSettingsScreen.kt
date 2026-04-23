package com.aethernovax.droidlocalai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.aethernovax.droidlocalai.data.GenerationPreferences
import com.aethernovax.droidlocalai.data.LlmPrefs
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextModelLlmSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { GenerationPreferences(context) }
    val llmSettings by prefs.llmSettings.collectAsState(initial = LlmPrefs())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Model Settings", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                SettingSlider(
                    title = "Temperature",
                    value = llmSettings.temperature,
                    range = 0f..2f,
                    steps = 20,
                    onValueChange = { scope.launch { prefs.updateLlmSettings(llmSettings.copy(temperature = it)) } }
                )
            }
            item {
                SettingSlider(
                    title = "Top P",
                    value = llmSettings.topP,
                    range = 0f..1f,
                    steps = 10,
                    onValueChange = { scope.launch { prefs.updateLlmSettings(llmSettings.copy(topP = it)) } }
                )
            }
            item {
                SettingSlider(
                    title = "Top K",
                    value = llmSettings.topK.toFloat(),
                    range = 0f..100f,
                    steps = 100,
                    onValueChange = { scope.launch { prefs.updateLlmSettings(llmSettings.copy(topK = it.toInt())) } }
                )
            }
            item {
                SettingSlider(
                    title = "Context Size",
                    value = llmSettings.contextSize.toFloat(),
                    range = 512f..8192f,
                    steps = 15,
                    onValueChange = { scope.launch { prefs.updateLlmSettings(llmSettings.copy(contextSize = it.toInt())) } }
                )
            }
            item {
                SettingSlider(
                    title = "Max New Tokens",
                    value = llmSettings.maxTokens.toFloat(),
                    range = 16f..4096f,
                    steps = 128,
                    onValueChange = { scope.launch { prefs.updateLlmSettings(llmSettings.copy(maxTokens = it.toInt())) } }
                )
            }
        }
    }
}

@Composable
fun SettingSlider(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                text = if (value >= 100) value.toInt().toString() else "%.2f".format(value),
                color = Color(0xFF4DD0E1)
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF4DD0E1),
                activeTrackColor = Color(0xFF4DD0E1),
                inactiveTrackColor = Color.Gray
            )
        )
    }
}
