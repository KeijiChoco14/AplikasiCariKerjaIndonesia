package com.example.lowongan

data class ArticleModel(
    var id: String = "",
    var title: String = "",
    var content: String = "",
    var imageUrl: String = "", // Nanti kalau mau pakai gambar
    var date: Long = 0
)