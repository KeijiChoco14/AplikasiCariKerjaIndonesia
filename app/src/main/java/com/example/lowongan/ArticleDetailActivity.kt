package com.example.lowongan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.lowongan.databinding.ActivityArticleDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Ambil data dari Intent
        val title = intent.getStringExtra("EXTRA_TITLE")
        val content = intent.getStringExtra("EXTRA_CONTENT")
        val image = intent.getStringExtra("EXTRA_IMAGE")
        val date = intent.getLongExtra("EXTRA_DATE", 0)

        // 2. Set ke tampilan
        binding.tvDetailTitle.text = title
        binding.tvDetailContent.text = content

        if (date > 0) {
            val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy • HH:mm", Locale("id", "ID"))
            binding.tvDetailDate.text = dateFormat.format(Date(date))
        }

        // Load Gambar
        if (!image.isNullOrEmpty()) {
            Glide.with(this)
                .load(image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.imgDetail)
        }

        // 3. LOGIC TOMBOL BACK (PENTING)
        // Karena kita pakai ImageButton biasa, bukan Toolbar bawaan
        binding.btnBack.setOnClickListener {
            finish() // Menutup activity dan kembali ke menu sebelumnya
        }
    }
}