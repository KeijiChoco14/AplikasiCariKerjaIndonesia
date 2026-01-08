package com.example.lowongan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lowongan.databinding.ActivityAdminLoginBinding
import com.google.firebase.auth.FirebaseAuth

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Init Firebase Auth
        auth = FirebaseAuth.getInstance()

        // 2. Tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                loginAdmin(email, pass)
            } else {
                Toast.makeText(this, "Isi email dan password dulu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginAdmin(email: String, pass: String) {
        // Tampilkan loading (opsional)
        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Loading..."

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Admin Berhasil!", Toast.LENGTH_SHORT).show()

                    // --- UPDATE BAGIAN INI ---
                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    startActivity(intent)
                    finish() // Agar user ga bisa back ke halaman login
                } else {
                    // Login Gagal
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.text = "MASUK"
                    Toast.makeText(this, "Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}