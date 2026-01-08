package com.example.lowongan

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.lowongan.databinding.ActivityAddEditArticleBinding
import com.google.firebase.firestore.FirebaseFirestore
import jp.wasabeef.richeditor.RichEditor // Import library

class AddEditArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditArticleBinding
    private lateinit var db: FirebaseFirestore

    private var isEditMode = false
    private var articleId: String? = null

    // Variabel bantu untuk toggle warna (contoh sederhana)
    private var isRedColor = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        setupRichEditor() // 1. Setup Editor
        setupToolbarButtons() // 2. Setup Tombol Format

        // 3. Cek Mode Edit
        if (intent.hasExtra("EXTRA_ID")) {
            isEditMode = true
            articleId = intent.getStringExtra("EXTRA_ID")

            binding.tvPageTitle.text = "Edit Artikel"
            binding.btnSave.text = "UPDATE ARTIKEL"
            binding.btnDelete.visibility = View.VISIBLE

            binding.etTitle.setText(intent.getStringExtra("EXTRA_TITLE"))
            binding.etImageUrl.setText(intent.getStringExtra("EXTRA_IMAGE"))

            // Set HTML konten ke Editor
            val contentHtml = intent.getStringExtra("EXTRA_CONTENT")
            binding.editor.html = contentHtml
        }

        binding.btnSave.setOnClickListener { saveArticle() }
        binding.btnDelete.setOnClickListener { showDeleteConfirmation() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRichEditor() {
        binding.editor.setPlaceholder("Tulis isi artikel lengkap di sini...")
        binding.editor.setEditorFontSize(16)
        binding.editor.setPadding(10, 10, 10, 10)
        // Warna text default hitam
        binding.editor.setEditorFontColor(Color.BLACK)
    }

    private fun setupToolbarButtons() {
        binding.actionBold.setOnClickListener { binding.editor.setBold() }
        binding.actionItalic.setOnClickListener { binding.editor.setItalic() }
        binding.actionUnderline.setOnClickListener { binding.editor.setUnderline() }

        binding.actionH1.setOnClickListener { binding.editor.setHeading(1) }
        binding.actionH2.setOnClickListener { binding.editor.setHeading(2) }

        // Contoh sederhana ganti warna (Toggle Hitam/Merah)
        // Jika ingin lebih canggih, harus pakai ColorPickerDialog
        binding.actionColor.setOnClickListener {
            isRedColor = !isRedColor
            if (isRedColor) {
                binding.editor.setTextColor(Color.RED)
                Toast.makeText(this, "Warna Teks: Merah", Toast.LENGTH_SHORT).show()
            } else {
                binding.editor.setTextColor(Color.BLACK)
                Toast.makeText(this, "Warna Teks: Hitam", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveArticle() {
        val title = binding.etTitle.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()

        // AMBIL DATA HTML DARI EDITOR, BUKAN TEXT BIASA
        val contentHtml = binding.editor.html

        if (title.isEmpty() || contentHtml == null || contentHtml.isEmpty()) {
            Toast.makeText(this, "Judul dan Isi tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false

        val articleMap = hashMapOf(
            "title" to title,
            "content" to contentHtml, // Simpan format HTML
            "imageUrl" to imageUrl,
            "date" to System.currentTimeMillis()
        )

        // ... Sisa logika simpan/update SAMA SEPERTI SEBELUMNYA ...
        if (isEditMode && articleId != null) {
            db.collection("articles").document(articleId!!)
                .update(articleMap as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Artikel berhasil diupdate!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    stopLoading()
                    Toast.makeText(this, "Gagal update: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            db.collection("articles")
                .add(articleMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Artikel berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    stopLoading()
                    Toast.makeText(this, "Gagal simpan: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Artikel?")
            .setMessage("Artikel ini akan dihapus permanen.")
            .setPositiveButton("Hapus") { _, _ -> deleteArticle() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteArticle() {
        if (articleId != null) {
            binding.progressBar.visibility = View.VISIBLE
            db.collection("articles").document(articleId!!)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Artikel dihapus!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    stopLoading()
                    Toast.makeText(this, "Gagal hapus", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun stopLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSave.isEnabled = true
    }
}