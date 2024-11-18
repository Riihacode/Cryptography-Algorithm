package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default fragment (HomeFragment) when activity starts
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, HomeFragment()) // Show HomeFragment first
                .commit()
        }

        // Set up BottomNavigationView item selection listener
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Show HomeFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_account -> {
                    // Show AccountFragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, AccountFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (!isLoggedIn) {
            // If not logged in, direct to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}