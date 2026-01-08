package com.example.lowongan

import android.content.Intent // PENTING: Tambahkan ini
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lowongan.databinding.ActivityPreferencesBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class PreferencesActivity : AppCompatActivity() {

    // -------- DATA MASTER --------
    private val positions = listOf(
        "Data & Technology", "Other", "Business & Product",
        "Finance & Risk", "Operations & Supply Chain",
        "Marketing & Sales", "Research & Quality"
    )

    private val educations = listOf(
        "accounting", "agriculture", "agronomy", "biology",
        "business administration", "business analytics", "chemistry",
        "civil engineering", "communication", "computer science",
        "data analytics", "data science", "economics",
        "electrical engineering", "engineering", "finance",
        "industrial engineering", "informatics engineering",
        "information systems", "information technology",
        "jurusan lainnya", "law", "management", "marketing",
        "mathematics", "mechanical engineering", "medicine",
        "pharmacy", "psychology", "statistics", "urban planning"
    )

    private val cities = listOf(
        "Genteng", "Jakarta Barat", "Jakarta Utara", "Jakarta Lainnya",
        "Bandung", "Bekasi", "Jakarta Timur", "Tangerang", "Jakarta Pusat",
        "Sidoarjo", "Jakarta Selatan", "Tangerang Selatan", "Depok",
        "Bogor", "Badung", "Gunungpati", "Sekadau", "Karawang", "Medan",
        "Malang", "Batu", "Semarang", "Surabaya", "Bogor Barat",
        "Samarinda", "Makassar", "Cilegon", "Tanah Bumbu", "Serang",
        "Pekanbaru", "Cilacap", "Mojokerto", "Cirebon", "Cimahi", "Sambas",
        "Palembang", "Bojonegoro", "Yogyakarta", "Boyolali", "Kendari",
        "Jembrana", "Gresik", "Deli Serdang"
    )

    private val tools = listOf(
        "SQL", "Excel", "Python", "R", "Power BI", "Tableau",
        "Google Data Studio", "Looker", "Metabase", "Google Analytics",
        "Adobe Analytics", "SPSS", "SAS", "Scikit-learn", "TensorFlow",
        "Keras", "PyTorch", "Snowflake", "BigQuery", "MongoDB",
        "MySQL", "PostgreSQL", "SQL Server", "Oracle", "Redshift",
        "Kafka", "API", "Java", "JavaScript", "Go",
        "AWS", "GCP", "Azure", "Google Cloud", "Kubernetes", "Docker",
        "Jenkins", "GitLab", "Amplitude", "Segment"
    )

    private val experiences = listOf(
        "0 tahun", "1 tahun", "2 tahun", "3 tahun",
        "4 tahun", "5 tahun", "6 tahun", "7+ tahun"
    )

    // -------- VIEW VARIABEL --------
    private lateinit var binding: ActivityPreferencesBinding
    private lateinit var chipGroupSelected: ChipGroup

    private lateinit var btnToggleJobPosition: ImageButton
    private lateinit var btnToggleJobLocation: ImageButton
    private lateinit var btnToggleEducation: ImageButton
    private lateinit var btnToggleTools: ImageButton
    private lateinit var btnToggleExperience: ImageButton

    private lateinit var chipGroupJobPosition: ChipGroup
    private lateinit var chipGroupJobLocation: ChipGroup
    private lateinit var chipGroupEducation: ChipGroup
    private lateinit var chipGroupTools: ChipGroup
    private lateinit var chipGroupExperience: ChipGroup

    private lateinit var btnSearchJob: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ----- INIT VIEW -----
        chipGroupSelected = findViewById(R.id.chipGroupSelected)

        btnToggleJobPosition = findViewById(R.id.btnToggleJobPosition)
        btnToggleJobLocation = findViewById(R.id.btnToggleJobLocation)
        btnToggleEducation = findViewById(R.id.btnToggleEducation)
        btnToggleTools = findViewById(R.id.btnToggleTools)
        btnToggleExperience = findViewById(R.id.btnToggleExperience)

        chipGroupJobPosition = findViewById(R.id.chipGroupJobPosition)
        chipGroupJobLocation = findViewById(R.id.chipGroupJobLocation)
        chipGroupEducation = findViewById(R.id.chipGroupEducation)
        chipGroupTools = findViewById(R.id.chipGroupTools)
        chipGroupExperience = findViewById(R.id.chipGroupExperience)

        btnSearchJob = findViewById(R.id.btnSearchJob)

        // ----- ISI CHIP GROUP AWAL -----
        addChipsToGroup(positions,   chipGroupJobPosition)
        addChipsToGroup(cities,      chipGroupJobLocation)
        addChipsToGroup(educations,  chipGroupEducation)
        addChipsToGroup(tools,       chipGroupTools)
        addChipsToGroup(experiences, chipGroupExperience)

        // ----- TOGGLE SECTION -----
        setupToggleListeners()

        // ----- LISTENER PERUBAHAN SELEKSI -----
        setupSelectionListeners()

        // ----- TOMBOL SEARCH JOB (LOGIKA UTAMA) -----
        btnSearchJob.setOnClickListener {
            // 1. Ambil semua teks dari Chip (LOGIKA LAMA TETAP SAMA)
            val selectedPositions = getSelectedChipTexts(chipGroupJobPosition)
            val selectedCities = getSelectedChipTexts(chipGroupJobLocation)
            val selectedEducations = getSelectedChipTexts(chipGroupEducation)
            val selectedTools = getSelectedChipTexts(chipGroupTools)
            val selectedExperiences = getSelectedChipTexts(chipGroupExperience)

            val queryParts = mutableListOf<String>()
            queryParts.addAll(selectedPositions)
            queryParts.addAll(selectedCities)
            queryParts.addAll(selectedTools)
            queryParts.addAll(selectedEducations)
            queryParts.addAll(selectedExperiences)

            val userQuery = queryParts.joinToString(" ")

            val allFilters = ArrayList<String>()
            allFilters.addAll(selectedPositions)
            allFilters.addAll(selectedCities)
            allFilters.addAll(selectedTools)
            allFilters.addAll(selectedEducations)
            allFilters.addAll(selectedExperiences)

            // 2. KIRIM KE MAIN ACTIVITY (Perbaikan Disini)
            val intent = Intent(this, MainActivity::class.java)

            // Beri "Bendera" agar MainActivity tahu harus buka tab Rekomendasi
            intent.putExtra("TARGET_FRAGMENT", "recommendation")

            // Kirim Data Query & Filter
            intent.putExtra("USER_QUERY", userQuery)
            intent.putStringArrayListExtra("SELECTED_FILTERS", allFilters)

            // Hapus activity sebelumnya dari stack agar tidak menumpuk (Back langsung keluar app/home)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
            finish()
        }
    }

    private fun setupToggleListeners() {
        btnToggleJobPosition.setOnClickListener { toggleSection(chipGroupJobPosition, btnToggleJobPosition) }
        btnToggleJobLocation.setOnClickListener { toggleSection(chipGroupJobLocation, btnToggleJobLocation) }
        btnToggleEducation.setOnClickListener { toggleSection(chipGroupEducation, btnToggleEducation) }
        btnToggleTools.setOnClickListener { toggleSection(chipGroupTools, btnToggleTools) }
        btnToggleExperience.setOnClickListener { toggleSection(chipGroupExperience, btnToggleExperience) }
    }

    private fun setupSelectionListeners() {
        val groups = listOf(chipGroupJobPosition, chipGroupJobLocation, chipGroupEducation, chipGroupTools, chipGroupExperience)
        groups.forEach { group ->
            group.setOnCheckedStateChangeListener { _, _ -> refreshSelectedChips() }
        }
    }

    // -------- FUNGSI UTAMA --------
    private fun addChipsToGroup(items: List<String>, chipGroup: ChipGroup) {
        chipGroup.removeAllViews()
        for (item in items) {
            val chip = Chip(this).apply {
                text = item
                isCheckable = true
                isClickable = true
            }
            chipGroup.addView(chip)
        }
    }

    private fun toggleSection(group: View, button: ImageButton) {
        if (group.visibility == View.VISIBLE) {
            group.visibility = View.GONE
            button.setImageResource(R.drawable.ic_add) // Pastikan icon ini ada
        } else {
            group.visibility = View.VISIBLE
            button.setImageResource(R.drawable.ic_remove) // Pastikan icon ini ada
        }
    }

    private fun getSelectedChipTexts(chipGroup: ChipGroup): List<String> {
        val result = mutableListOf<String>()
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                chip.text?.toString()?.let { result.add(it) }
            }
        }
        return result
    }

    private fun refreshSelectedChips() {
        chipGroupSelected.removeAllViews()

        val allGroups = listOf(chipGroupJobPosition, chipGroupJobLocation, chipGroupEducation, chipGroupTools, chipGroupExperience)

        for (group in allGroups) {
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as Chip
                if (chip.isChecked) {
                    val selectedChip = Chip(this).apply {
                        text = chip.text
                        isCloseIconVisible = true
                        isCheckable = false
                        setOnCloseIconClickListener {
                            chip.isChecked = false
                            refreshSelectedChips()
                        }
                    }
                    chipGroupSelected.addView(selectedChip)
                }
            }
        }
    }
}