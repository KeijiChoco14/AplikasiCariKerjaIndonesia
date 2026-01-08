package com.example.lowongan

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.lowongan.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. DEFINISI LISTENER (Simpan di variabel dulu)
        val navListener = com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_recommendation -> {
                    replaceFragment(RecommendationFragment())
                    true
                }
                R.id.nav_article -> {
                    replaceFragment(ArticleFragment())
                    true
                }
                else -> false
            }
        }

        // 2. CEK KIRIMAN DATA (INTENT)
        val targetFragment = intent.getStringExtra("TARGET_FRAGMENT")

        if (targetFragment == "recommendation") {
            // --- KASUS A: Buka dari Filter ---
            val userQuery = intent.getStringExtra("USER_QUERY")
            val selectedFilters = intent.getStringArrayListExtra("SELECTED_FILTERS")

            val fragment = RecommendationFragment()
            val bundle = Bundle()
            bundle.putString("USER_QUERY", userQuery)
            bundle.putStringArrayList("SELECTED_FILTERS", selectedFilters)
            fragment.arguments = bundle

            replaceFragment(fragment)

            // TRICK PENTING: Matikan listener sebelum set item, lalu nyalakan lagi
            // Agar fragment yang barusan kita buat TIDAK tertimpa
            binding.bottomNavigation.setOnItemSelectedListener(null)
            binding.bottomNavigation.selectedItemId = R.id.nav_recommendation
            binding.bottomNavigation.setOnItemSelectedListener(navListener)

        } else {
            // --- KASUS B: Buka Normal ---
            replaceFragment(HomeFragment())

            binding.bottomNavigation.setOnItemSelectedListener(null)
            binding.bottomNavigation.selectedItemId = R.id.nav_home
            binding.bottomNavigation.setOnItemSelectedListener(navListener)
        }
    }
}