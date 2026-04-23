package com.aethernovax.droidlocalai.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextModelSettingsScreen(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF121212))) {
        TopAppBar(
            title = { Text("Setting", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Group 1
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(1.dp, Color.White)
            ) {
                SettingsItem(
                    icon = Icons.Default.Settings,
                    title = "Model Settings",
                    onClick = { navController.navigate(com.aethernovax.droidlocalai.navigation.Screen.TextAiLlmSettings.route) }
                )
                HorizontalDivider(color = Color.White)
                SettingsItem(
                    icon = Icons.Default.Wifi,
                    title = "Connection Settings",
                    onClick = { /* TODO */ }
                )
            }
            // Group 2
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(1.dp, Color.White)
            ) {
                SettingsItem(
                    icon = Icons.Default.Star,
                    title = "Favorites",
                    onClick = { /* TODO */ }
                )
                HorizontalDivider(color = Color.White)
                SettingsItem(
                    icon = Icons.Default.Email,
                    title = "Feedback",
                    onClick = { /* TODO */ }
                )
            }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.White)
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Performa, system, advance setting", color = Color.Gray, fontSize = 12.sp)
        }
    }
}
