package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.AESAlgorithm
import com.dicoding.cryptographyalgorithm.databinding.ActivitySignupBinding
import javax.crypto.SecretKey

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val secretKey: SecretKey by lazy {
        AESAlgorithm.generateAESKeyFromString("1234567890123456")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        binding.signupButton.setOnClickListener { handleSignup() }
        binding.loginButtonInSignup.setOnClickListener { navigateToLogin() }
    }

    // Fungsi untuk menangani proses signup
    private fun handleSignup() {
        val username = binding.usernameSignupEditText.text.toString().trim()
        val email = binding.emailSignupEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.passwordSignupEditTextConf.text.toString()

        if (areFieldsValid(username, email, password, confirmPassword)) {
            if (password == confirmPassword) {
                if (isUsernameAvailable(username)) {
                    registerUser(username, email, password)
                } else {
                    showToast("Username already exists")
                }
            } else {
                showToast("Passwords do not match")
            }
        } else {
            showToast("Please fill in all fields")
        }
    }

    // Fungsi untuk memvalidasi input pengguna
    private fun areFieldsValid(username: String, email: String, password: String, confirmPassword: String): Boolean {
        return username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
    }

    // Fungsi untuk mengecek ketersediaan username
    private fun isUsernameAvailable(username: String): Boolean {
        return sharedPreferences.getString("${username}_username", null) == null
    }

    // Fungsi untuk mendaftarkan user baru
    private fun registerUser(username: String, email: String, password: String) {
        val encryptedPassword = encryptPassword(password)

        saveUserData(username, email, encryptedPassword)
        showToast("Registration successful")
        navigateToLogin()
    }

    // Fungsi untuk mengenkripsi password
    private fun encryptPassword(password: String): String {
        return AESAlgorithm.encryptAES(password, secretKey)
    }

    // Fungsi untuk menyimpan data pengguna ke SharedPreferences
    private fun saveUserData(username: String, email: String, encryptedPassword: String) {
        sharedPreferences.edit()
            .putString("${email}_username", username)
            .putString("${email}_password", encryptedPassword)
            .apply()
    }

    // Fungsi untuk berpindah ke LoginActivity
    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    // Fungsi untuk menampilkan pesan toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}