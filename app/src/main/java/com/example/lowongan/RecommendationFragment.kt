package com.example.lowongan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lowongan.databinding.FragmentRecommendationBinding // Pastikan nama ini sesuai XML
import com.google.android.material.chip.Chip
import org.json.JSONArray

class RecommendationFragment : Fragment() {

    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!

    private lateinit var jobAdapter: JobAdapter
    private lateinit var onnxHelper: OnnxHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Tambahkan fungsi ini di dalam class RecommendationFragment
    private fun setupFilterChips(filters: ArrayList<String>) {
        // 1. Bersihkan chip lama agar tidak duplikat
        binding.chipGroupSelectedFilters.removeAllViews()

        for (filter in filters) {
            // 2. Buat objek Chip baru
            val chip = Chip(requireContext())
            chip.text = filter
            chip.isCloseIconVisible = true // Tampilkan icon 'X'

            // --- PENYESUAIAN TAMPILAN (STYLING) ---

            // Background: Biru #0288D1 (Sesuai Header)
            chip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#0288D1"))

            // Teks: Putih (Agar kontras dengan biru)
            chip.setTextColor(android.graphics.Color.WHITE)

            // Icon Silang (X): Putih
            chip.closeIconTint = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)

            // Style font (Opsional, agar sedikit lebih tebal)
            chip.typeface = android.graphics.Typeface.DEFAULT_BOLD

            // --- AKSI KLIK ---
            chip.setOnCloseIconClickListener {
                // Hapus chip dari tampilan
                binding.chipGroupSelectedFilters.removeView(chip)

                // (Opsional) Refresh pencarian otomatis saat filter dihapus
                // loadDataInBackground(binding.etSearch.text.toString())
            }

            // 3. Masukkan Chip ke dalam Layout (ChipGroup)
            binding.chipGroupSelectedFilters.addView(chip)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onnxHelper = OnnxHelper(requireContext())

        // --- Setup Tombol Filter ---
        binding.btnFilter.setOnClickListener {
            val intent = Intent(requireContext(), PreferencesActivity::class.java)
            startActivity(intent)
        }

        // --- AMBIL DATA DARI ARGUMENTS (BUNDLE) ---
        // Jika argumen kosong (buka dari menu bawah), pakai default ""
        val bundleQuery = arguments?.getString("USER_QUERY") ?: ""
        val bundleFilters = arguments?.getStringArrayList("SELECTED_FILTERS") ?: arrayListOf()

        // Tampilkan Chip Filter (jika ada)
        if (bundleFilters.isNotEmpty()) {
            setupFilterChips(bundleFilters) // Pastikan fungsi setupFilterChips kamu pindahkan ke Fragment juga
        }

        // Isi search bar jika ada query
        if (bundleQuery.isNotEmpty()){
            binding.etSearch.setText(bundleQuery)
        }

        // Load data
        loadDataInBackground(bundleQuery)
    }

    private fun loadDataInBackground(userQuery: String) {
        // Jika query kosong (User buka tab manual), tampilkan pesan "Silakan Filter"
        if (userQuery.isBlank()) {
            binding.progressBar.visibility = View.GONE
            binding.rvRecommendations.visibility = View.GONE
            binding.tvResultCount.text = "Gunakan tombol filter untuk mencari."
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.rvRecommendations.visibility = View.GONE
        binding.tvResultCount.text = "Memproses data..."

        Thread {
            val resultList = processCsvAndOnnx(userQuery)

            activity?.runOnUiThread {
                if (_binding == null) return@runOnUiThread

                binding.progressBar.visibility = View.GONE

                if (resultList.isNotEmpty()) {
                    binding.rvRecommendations.visibility = View.VISIBLE
                    binding.tvResultCount.text = "${resultList.size} Pekerjaan Ditemukan"

                    jobAdapter = JobAdapter(resultList) { job ->
                        val intent = Intent(requireContext(), JobDetailActivity::class.java)
                        intent.putExtra("EXTRA_POS", job.position_category)
                        intent.putExtra("EXTRA_COMP", job.perusahaan)
                        intent.putExtra("EXTRA_CITY", job.kota)
                        intent.putExtra("EXTRA_TOOLS", job.tools)
                        intent.putExtra("EXTRA_SCORE", job.score)
                        intent.putExtra("EXTRA_DESKRIPSI_LENGKAP", job.deskripsi_lengkap)
                        startActivity(intent)
                    }
                    binding.rvRecommendations.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvRecommendations.adapter = jobAdapter
                } else {
                    binding.rvRecommendations.visibility = View.GONE
                    binding.tvResultCount.text = "Tidak ditemukan kecocokan."
                }
            }
        }.start()
    }

    private fun processCsvAndOnnx(userQuery: String): List<JobModel> {
        val jobList = mutableListOf<JobModel>()

        // Cek Log: Apakah Query sampai disini?
        Log.d("SEARCH_DEBUG", "Menerima Query: '$userQuery'")

        if (userQuery.isBlank()) {
            return emptyList()
        }

        try {
            val inputStream = requireContext().assets.open("lowongan_android_clean.json")
            val jsonText = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonText)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)

                val rawId = obj.optString("id", "0")
                val rawCompany = obj.optString("company", "-")
                val rawCity = obj.optString("city", "-")
                val rawTools = obj.optString("tools", "-")
                val rawPosition = obj.optString("position", "Job Position")
                val rawPositionCategory = obj.optString("position_category", "Other")
                val rawDeskripsiLengkap = obj.optString("description", "Deskripsi tidak tersedia.")
                val rawContent = obj.optString("content", rawDeskripsiLengkap)

                // Hitung Score
                val score = onnxHelper.calculateWeightedScore(
                    query = userQuery,
                    position = rawPosition,
                    tools = rawTools,
                    description = rawContent
                ).toDouble()

                // LOG HASIL PERHITUNGAN (Hanya yang ada isinya)
                if (score > 0.0) {
                    Log.d("SEARCH_MATCH", "Match! Score: $score | Pos: $rawPosition")

                    jobList.add(
                        JobModel(rawId, rawCompany, rawCity, rawTools, rawPosition, rawPositionCategory, score, rawDeskripsiLengkap)
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("JSON_ERROR", "Error: ${e.message}")
        }

        Log.d("SEARCH_DEBUG", "Total Ditemukan: ${jobList.size}")
        return jobList.sortedByDescending { it.score }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        onnxHelper.close() // Tutup ONNX agar tidak memori leak
    }
}