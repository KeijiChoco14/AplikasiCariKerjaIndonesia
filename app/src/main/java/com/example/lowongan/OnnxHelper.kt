package com.example.lowongan

import android.content.Context
import android.util.Log
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.util.Collections

class OnnxHelper(context: Context) {
    private var ortEnv: OrtEnvironment? = null
    private var ortSession: OrtSession? = null
    private var isModelLoaded = false

    init {
        try {
            ortEnv = OrtEnvironment.getEnvironment()
            // Pastikan file tfidf_model.onnx ada di assets
            val modelBytes = context.assets.open("tfidf_model.onnx").readBytes()
            ortSession = ortEnv?.createSession(modelBytes)
            isModelLoaded = true
        } catch (e: Exception) {
            Log.e("ONNX_ERROR", "Gagal memuat model: ${e.message}")
            isModelLoaded = false
        }
    }

    /**
     * Fungsi baru untuk menghitung skor dengan BOBOT.
     * Judul (Position) lebih penting daripada Deskripsi.
     */
    // Tambahkan/Update fungsi ini di OnnxHelper.kt
    private fun calculateRobustMatch(query: String, text: String): Float {
        // PERBAIKAN: Ganti "[^a-z0-9]" dengan " " (SPASI)
        val cleanQuery = query.lowercase().replace(Regex("[^a-z0-9]"), " ").trim()
        val cleanText = text.lowercase().replace(Regex("[^a-z0-9]"), " ").trim()

        if (cleanQuery.isBlank() || cleanText.isBlank()) return 0f

        val queryWords = cleanQuery.split(" ").filter { it.isNotBlank() }.toSet()
        val textWords = cleanText.split(" ").filter { it.isNotBlank() }.toSet()

        // ... (kode selanjutnya sama)
        if (queryWords.isEmpty()) return 0f
        val intersection = queryWords.intersect(textWords)
        return intersection.size.toFloat() / queryWords.size.toFloat()
    }

    // Pastikan fungsi ini ada
    fun calculateWeightedScore(query: String, position: String, tools: String, description: String): Float {
        val scoreTitle = calculateRobustMatch(query, position)
        val scoreTools = calculateRobustMatch(query, tools)
        val scoreDesc = calculateRobustMatch(query, description)
        return (scoreTitle * 0.5f) + (scoreTools * 0.3f) + (scoreDesc * 0.2f)
    }

    fun close() {
        ortSession?.close()
        ortEnv?.close()
    }
}