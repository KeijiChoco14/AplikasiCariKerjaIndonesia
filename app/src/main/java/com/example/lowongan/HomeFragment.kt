package com.example.lowongan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.lowongan.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Init Firestore
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        // 1. Logic Tombol Navigasi ke Rekomendasi
        binding.btnGoToRecommendation.setOnClickListener {
            val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNav?.selectedItemId = R.id.nav_recommendation
        }

        binding.cardBanner.setOnClickListener {
            Toast.makeText(context, "Semangat cari kerjanya! EHEHEHEH", Toast.LENGTH_SHORT).show()
        }

        // 2. LOAD 2 TIPS TERBARU
        loadRecentTips()
    }

    private fun loadRecentTips() {
        // Query: Ambil koleksi 'articles', urutkan tanggal DESC (terbaru diatas), ambil 2 saja
        db.collection("articles")
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(2)
            .addSnapshotListener { snapshots, e ->

                // Cek jika view sudah hancur (user pindah halaman), stop proses biar ga crash
                if (_binding == null) return@addSnapshotListener

                if (e != null) {
                    binding.tvLoadingTips.text = "Gagal memuat tips."
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    binding.tvLoadingTips.visibility = View.GONE

                    val articles = snapshots.toObjects(ArticleModel::class.java)

                    // --- ARTIKEL PERTAMA (TERBARU) ---
                    if (articles.isNotEmpty()) {
                        val art1 = articles[0]
                        binding.cardTips1.visibility = View.VISIBLE
                        binding.tvTipsTitle1.text = art1.title

                        // Load Gambar 1
                        if (art1.imageUrl.isNotEmpty()) {
                            Glide.with(this).load(art1.imageUrl).centerCrop().into(binding.imgTips1)
                        }

                        // Klik Card 1 -> Buka Detail
                        binding.cardTips1.setOnClickListener {
                            openDetailArticle(art1)
                        }
                    }

                    // --- ARTIKEL KEDUA ---
                    if (articles.size >= 2) {
                        val art2 = articles[1]
                        binding.cardTips2.visibility = View.VISIBLE
                        binding.tvTipsTitle2.text = art2.title

                        // Load Gambar 2
                        if (art2.imageUrl.isNotEmpty()) {
                            Glide.with(this).load(art2.imageUrl).centerCrop().into(binding.imgTips2)
                        }

                        // Klik Card 2 -> Buka Detail
                        binding.cardTips2.setOnClickListener {
                            openDetailArticle(art2)
                        }
                    } else {
                        // Kalau cuma ada 1 artikel, sembunyikan kartu ke-2
                        binding.cardTips2.visibility = View.GONE
                    }

                } else {
                    // Kalau tidak ada artikel sama sekali
                    binding.tvLoadingTips.text = "Belum ada tips karir saat ini."
                    binding.cardTips1.visibility = View.GONE
                    binding.cardTips2.visibility = View.GONE
                }
            }
    }

    private fun openDetailArticle(article: ArticleModel) {
        val intent = Intent(requireContext(), ArticleDetailActivity::class.java)
        intent.putExtra("EXTRA_TITLE", article.title)
        intent.putExtra("EXTRA_CONTENT", article.content)
        intent.putExtra("EXTRA_IMAGE", article.imageUrl)
        intent.putExtra("EXTRA_DATE", article.date)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}