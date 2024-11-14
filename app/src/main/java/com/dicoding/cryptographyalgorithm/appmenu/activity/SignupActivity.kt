package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.AESAlgorithm
import com.dicoding.cryptographyalgorithm.databinding.ActivitySignupBinding
import javax.crypto.SecretKey

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding  // Binding class untuk layout
    private lateinit var sharedPreferences: SharedPreferences
    private val secretKey: SecretKey by lazy {
        AESAlgorithm.generateAESKeyFromString("1234567890123456")  // Ganti dengan kunci yang Anda gunakan
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)  // Menggunakan root view dari binding

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Set click listener untuk tombol Sign Up
        binding.btnSignUp.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                if (isUsernameAvailable(username)) {
                    // Mendaftarkan user dan menyimpan password terenkripsi
                    registerUser(username, password)
                } else {
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk memeriksa apakah username sudah ada di SharedPreferences
    private fun isUsernameAvailable(username: String): Boolean {
        return sharedPreferences.getString(username, null) == null
    }

    // Fungsi untuk menyimpan data pengguna yang terdaftar
    private fun registerUser(username: String, password: String) {
        val encryptedPassword = AESAlgorithm.encryptAES(password, secretKey)

        // Menyimpan data dalam SharedPreferences
        sharedPreferences.edit().putString(username, encryptedPassword).apply()

        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

        // Bisa langsung menuju ke halaman login setelah registrasi
        finish()  // Menutup halaman SignUp dan kembali ke halaman sebelumnya
    }
}