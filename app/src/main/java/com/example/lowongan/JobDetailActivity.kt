package com.example.lowongan

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lowongan.databinding.ActivityJobDetailBinding
import com.google.android.material.chip.Chip

class JobDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Ambil Data
        val position = intent.getStringExtra("EXTRA_POS") ?: "-"
        val company = intent.getStringExtra("EXTRA_COMP") ?: "-"
        val city = intent.getStringExtra("EXTRA_CITY") ?: "-"
        val tools = intent.getStringExtra("EXTRA_TOOLS") ?: ""
        val score = intent.getDoubleExtra("EXTRA_SCORE", 0.0)

        // AMBIL VARIABEL deskripsi_lengkap
        val deskripsiLengkap = intent.getStringExtra("EXTRA_DESKRIPSI_LENGKAP") ?: "Deskripsi tidak tersedia."

        // 2. Tampilkan Data
        binding.tvDetailPosition.text = position
        binding.tvDetailCompany.text = "$company • $city"

        // Tampilkan deskripsi lengkap di sini
        binding.tvDetailDescription.text = deskripsiLengkap

        val percentage = (score * 100).toInt()
        binding.tvDetailScore.text = "$percentage% Match Score"

        // 3. Setup Tools Chips
        binding.chipGroupDetailTools.removeAllViews()
        val toolsArray = tools.split(",").map { it.trim() }
        for (tool in toolsArray) {
            if (tool.isNotEmpty()) {
                val chip = Chip(this).apply {
                    text = tool
                    setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E1F5FE")))
                    setTextColor(Color.parseColor("#0277BD"))
                    isCheckable = false
                    isClickable = false
                }
                binding.chipGroupDetailTools.addView(chip)
            }
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnApply.setOnClickListener {
            Toast.makeText(this, "Lamaran terkirim!", Toast.LENGTH_SHORT).show()
        }
    }
}