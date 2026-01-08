package com.example.lowongan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lowongan.databinding.FragmentArticleBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: UserArticleAdapter
    private var articleList = mutableListOf<ArticleModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        // 1. Setup Tombol Login Admin (Yang sudah kamu buat sebelumnya)
        binding.btnAdminLogin.setOnClickListener {
            startActivity(Intent(requireContext(), AdminLoginActivity::class.java))
        }

        // 2. Setup RecyclerView
        setupRecyclerView()

        // 3. Load Data Artikel
        loadArticles()
    }

    private fun setupRecyclerView() {
        // PERBAIKAN: Kita butuh RecyclerView di XML ArticleFragment
        // Pastikan di fragment_article.xml kamu sudah menambahkan RecyclerView dengan id rvArticlesUser

        adapter = UserArticleAdapter(articleList) { article ->
            // KLIK ITEM: Buka Detail
            val intent = Intent(requireContext(), ArticleDetailActivity::class.java)
            intent.putExtra("EXTRA_TITLE", article.title)
            intent.putExtra("EXTRA_CONTENT", article.content)
            intent.putExtra("EXTRA_IMAGE", article.imageUrl)
            intent.putExtra("EXTRA_DATE", article.date)
            startActivity(intent)
        }

        // Cek apakah binding punya rvArticlesUser (kalau belum ada di XML, tambahkan dulu)
        // Anggap saja kita update XML-nya sebentar lagi.
        binding.rvArticlesUser.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticlesUser.adapter = adapter
    }

    private fun loadArticles() {
        // ... kode query sebelumnya ...

        db.collection("articles")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->

                // --- TAMBAHKAN BARIS INI (PENYELAMAT) ---
                // Jika binding null (artinya fragment sudah hancur/tutup), stop proses!
                if (_binding == null) return@addSnapshotListener
                // ----------------------------------------

                if (e != null) {
                    // Gunakan context yang aman
                    if (context != null) {
                        Toast.makeText(context, "Gagal memuat artikel", Toast.LENGTH_SHORT).show()
                    }
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvArticlesUser.visibility = View.VISIBLE

                    articleList.clear()
                    for (doc in snapshots) {
                        val article = doc.toObject(ArticleModel::class.java)
                        // Simpan ID agar bisa dipakai kalau perlu
                        article.id = doc.id
                        articleList.add(article)
                    }
                    adapter.updateData(articleList)
                } else {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvArticlesUser.visibility = View.GONE
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}