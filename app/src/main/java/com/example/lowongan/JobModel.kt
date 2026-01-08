package com.example.lowongan

data class JobModel(
    val id: String,
    val perusahaan: String,
    val kota: String,
    val tools: String,
    val position_category: String,
    val position : String,
    val score: Double,
    val deskripsi_lengkap: String
)