package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.AESAlgorithm
import com.dicoding.cryptographyalgorithm.databinding.ActivityLoginBinding
import javax.crypto.SecretKey

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val secretKey: SecretKey by lazy {
        AESAlgorithm.generateAESKeyFromString("1234567890123456")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        logSharedPreferencesData()
        checkIfUserIsLoggedIn()

        binding.loginNormal.setOnClickListener { handleLogin() }
        binding.signupButtonInLogin.setOnClickListener { navigateToSignup() }
    }

    // Fungsi untuk log semua data di SharedPreferences
    private fun logSharedPreferencesData() {
        val allEntries = sharedPreferences.all
        for ((key, value) in allEntries) {
            Log.d("SharedPreferences", "$key: $value")
        }
    }

    // Fungsi untuk mengecek apakah user sudah login
    private fun checkIfUserIsLoggedIn() {
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            navigateToMainActivity()
        }
    }

    // Fungsi untuk menangani login
    private fun handleLogin() {
        val email = binding.emailLoginEditText.text.toString().trim()
        val password = binding.passwordLoginEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            val storedEncryptedPassword = sharedPreferences.getString("${email}_password", null)

            if (storedEncryptedPassword != null) {
                validatePassword(email, password, storedEncryptedPassword)
            } else {
                showToast("Email not found")
            }
        } else {
            showToast("Please enter both email and password")
        }
    }

    // Fungsi untuk memvalidasi password
    private fun validatePassword(email: String, password: String, storedEncryptedPassword: String) {
        try {
            val decryptedPassword = AESAlgorithm.decryptAES(storedEncryptedPassword, secretKey)

            if (password == decryptedPassword) {
                saveLoginState(email)
                showToast("Login successful")
                navigateToMainActivity()
            } else {
                showToast("Invalid password")
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Error decrypting password: ${e.message}")
            showToast("Error decrypting password")
        }
    }

    // Fungsi untuk menyimpan status login
    private fun saveLoginState(email: String) {
        sharedPreferences.edit()
            .putBoolean("is_logged_in", true)
            .putString("current_email", email)
            .apply()
    }

    // Fungsi untuk menampilkan toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Fungsi untuk berpindah ke MainActivity
    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // Fungsi untuk berpindah ke SignupActivity
    private fun navigateToSignup() {
        startActivity(Intent(this, SignupActivity::class.java))
    }
}


