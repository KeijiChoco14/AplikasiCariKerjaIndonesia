package com.example.lowongan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lowongan.databinding.ActivityAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: AdminArticleAdapter
    private var articleList = mutableListOf<ArticleModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init Firestore
        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        loadArticles()

        // Tombol Tambah
        binding.fabAddArticle.setOnClickListener {
            val intent = Intent(this, AddEditArticleActivity::class.java)
            startActivity(intent)
        }

        // Tombol Kembali (Logout/Exit)
        binding.btnBack.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish() // Kembali ke login atau tutup dashboard
        }
    }

    private fun setupRecyclerView() {
        adapter = AdminArticleAdapter(articleList) { article ->
            // Kirim data yang mau diedit
            val intent = Intent(this, AddEditArticleActivity::class.java)
            intent.putExtra("EXTRA_ID", article.id)
            intent.putExtra("EXTRA_TITLE", article.title)
            intent.putExtra("EXTRA_CONTENT", article.content)
            intent.putExtra("EXTRA_IMAGE", article.imageUrl)
            startActivity(intent)
        }
        binding.rvArticles.layoutManager = LinearLayoutManager(this)
        binding.rvArticles.adapter = adapter
    }

    private fun loadArticles() {
        binding.progressBar.visibility = View.VISIBLE

        // Ambil koleksi "articles", urutkan berdasarkan tanggal descending (terbaru diatas)
        db.collection("articles")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                binding.progressBar.visibility = View.GONE

                if (e != null) {
                    Toast.makeText(this, "Error load data: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    // ADA DATA
                    binding.tvEmptyState.visibility = View.GONE
                    binding.rvArticles.visibility = View.VISIBLE

                    articleList.clear()
                    for (doc in snapshots) {
                        val article = doc.toObject(ArticleModel::class.java)
                        article.id = doc.id // Simpan ID dokumen untuk keperluan Edit/Hapus
                        articleList.add(article)
                    }
                    adapter.updateData(articleList)

                } else {
                    // TIDAK ADA DATA
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.rvArticles.visibility = View.GONE
                }
            }
    }
}