package com.aethernovax.droidlocalai.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Pastikan kamu mengimpor ChatManager yang sudah kita bahas sebelumnya
// import com.namapaketkamu.droidlocalai.ChatManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(chatManager: ChatManager) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF121212))) {
        TopAppBar(
            title = { Text("Chats", color = Color.White, fontWeight = FontWeight.Bold) },
            actions = {
                OutlinedButton(onClick = {}, border = BorderStroke(1.dp,
                    Color(0xFF4DD0E1)
                )) {
                    Text("+ New", color = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val chats = listOf(
                "Project 1" to "31 menit ago",
                "Project 2" to "1 day ago",
                "Project 3" to "2 day ago"
            )
            items(chats) { (title, time) ->
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(time, color = Color.Gray, fontSize = 12.sp)
                        }
                        Text("isi Messege.... 1", color = Color.Gray)
                    }
                }
            }
        }
    }
}
