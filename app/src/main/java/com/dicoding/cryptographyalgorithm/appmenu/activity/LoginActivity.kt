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
import com.dicoding.cryptographyalgorithm.databinding.ActivityLoginBinding
import javax.crypto.SecretKey

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding  // Binding class untuk layout
    private lateinit var sharedPreferences: SharedPreferences
    private val secretKey: SecretKey by lazy {
        AESAlgorithm.generateAESKeyFromString("1234567890123456")  // Ganti dengan kunci yang Anda gunakan
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)  // Menggunakan root view dari binding

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // Set click listener untuk tombol login
        binding.btnLogin.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val password = binding.edtPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Memeriksa apakah username dan password cocok
                val storedEncryptedPassword = sharedPreferences.getString(username, null)

                if (storedEncryptedPassword != null) {
                    val decryptedPassword = AESAlgorithm.decryptAES(storedEncryptedPassword, secretKey)
                    if (password == decryptedPassword) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                        // Lanjutkan ke activity berikutnya
                    } else {
                        Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menyimpan data pengguna saat pertama kali login atau mendaftar
    fun registerUser(username: String, password: String) {
        val encryptedPassword = AESAlgorithm.encryptAES(password, secretKey)

        // Menyimpan data dalam SharedPreferences
        sharedPreferences.edit().putString(username, encryptedPassword).apply()

        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
    }
}