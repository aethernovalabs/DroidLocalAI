package com.aethernovax.droidlocalai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Pastikan kamu mengimpor ChatManager yang sudah kita bahas sebelumnya
// import com.namapaketkamu.droidlocalai.ChatManager

@Composable
fun ChatScreen(chatManager: ChatManager) {
    // Menyimpan daftar pesan (Riwayat chat di layar)
    var messages by remember { mutableStateOf(listOf<String>()) }
    // Menyimpan teks yang sedang diketik oleh pengguna
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // 1. AREA DAFTAR PESAN
        // LazyColumn berguna agar pesan bisa di-scroll jika sudah penuh
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                Text(
                    text = message,
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
                HorizontalDivider() // Garis pembatas antar pesan
            }
        }

        // 2. AREA MENGETIK DAN TOMBOL KIRIM
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Kotak untuk mengetik pesan
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ketik pesan ke AI...") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Tombol Kirim
            Button(onClick = {
                if (inputText.isNotBlank()) {
                    // A. Tampilkan pesan kita di layar
                    messages = messages + "👤 Kamu: $inputText"

                    // B. Proses teks ke sistem Lorebook & AI (ChatManager)
                    val aiResponse = chatManager.processUserInput(inputText)

                    // C. Tampilkan balasan AI di layar
                    messages = messages + "🤖 AI: $aiResponse"

                    // D. Kosongkan kotak ketik
                    inputText = ""
                }
            }) {
                Text("Kirim")
            }
        }
    }
}