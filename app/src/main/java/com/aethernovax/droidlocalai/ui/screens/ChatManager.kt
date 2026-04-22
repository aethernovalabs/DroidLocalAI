package com.aethernovax.droidlocalai.ui.screens

// File: ChatManager.kt
// Tujuan: Mengelola obrolan pengguna dan menyisipkan konteks Lorebook (RAG) secara offline.

class ChatManager {

    /**
     * 1. DATABASE LOREBOOK SEDERHANA
     * Di sini kita menyimpan ingatan atau latar belakang cerita.
     * Untuk aplikasi skala besar, kita nantinya bisa mengubah ini menjadi
     * database lokal seperti SQLite atau Vector Database.
     */
    private val lorebook = mapOf(
        "naga" to "Naga di dunia ini bernapas es, bukan api.",
        "kerajaan" to "Kerajaan ini dipimpin oleh Ratu Elara yang bijaksana dan adil."
    )

    /**
     * 2. FUNGSI UTAMA PEMROSESAN CHAT
     * Fungsi ini dipanggil saat tombol "Kirim" ditekan di layar Chat.
     * @param userInput Teks yang diketik oleh pengguna.
     * @return Balasan dari AI.
     */
    fun processUserInput(userInput: String): String {
        // A. Ambil konteks yang relevan dari Lorebook
        val context = retrieveLorebookContext(userInput)

        // B. Buat perintah gabungan (Prompt Engineering)
        // Kita menggabungkan ingatan dari Lorebook dengan pertanyaan asli pengguna.
        val finalPrompt = """
            Sistem: Kamu adalah karakter AI yang pintar. Gunakan informasi latar belakang berikut jika relevan dengan obrolan:
            $context
            
            Pengguna: $userInput
            AI: 
        """.trimIndent()

        // C. Kirim ke mesin LLM lokal kita
        return sendToLocalLLM(finalPrompt)
    }

    /**
     * 3. FUNGSI PENCARIAN (RAG SEDERHANA)
     * Mencari apakah ada kata di input pengguna yang cocok dengan Lorebook kita.
     */
    private fun retrieveLorebookContext(input: String): String {
        val foundLore = mutableListOf<String>()
        val lowerInput = input.lowercase()

        // Periksa setiap kata kunci di dalam lorebook
        for ((keyword, lore) in lorebook) {
            if (lowerInput.contains(keyword)) {
                foundLore.add(lore) // Jika cocok, tambahkan ke ingatan yang ditemukan
            }
        }

        // Jika ada ingatan yang cocok, rangkai menjadi satu teks.
        return if (foundLore.isNotEmpty()) {
            "Latar Belakang Cerita:\n" + foundLore.joinToString("\n")
        } else {
            "" // Jika tidak ada, kembalikan teks kosong agar tidak membingungkan AI.
        }
    }

    /**
     * 4. JEMBATAN KE MESIN C++ (llama.cpp)
     * Ini adalah fungsi yang nantinya akan meneruskan teks ke model .gguf secara offline.
     */
    private fun sendToLocalLLM(prompt: String): String {
        // Kita cetak prompt-nya ke sistem log Android agar parameter "prompt" digunakan
        println("=== PROMPT YANG DIKIRIM KE AI ===")
        println(prompt)
        println("=================================")

        return "Ini simulasi balasan AI... (Fitur llama.cpp akan kita pasang selanjutnya!)"
    }
}