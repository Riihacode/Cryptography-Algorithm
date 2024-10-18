package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.RC4Algorithm
import com.dicoding.cryptographyalgorithm.databinding.ActivityRc4Binding

class RC4Activity : AppCompatActivity() {
    private lateinit var binding: ActivityRc4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRc4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.encryptButton.setOnClickListener {
            val plainText = binding.inputText.text.toString()
            val key = binding.inputKey.text.toString()

            try {
                if (plainText.isEmpty()) {
                    binding.encryptedText.text = getString(R.string.text_cannot_be_empty)
                    return@setOnClickListener
                }
                if (key.isEmpty()) {
                    binding.encryptedText.text = getString(R.string.key_cannot_be_empty)
                    return@setOnClickListener
                }

                //val encryptedText = encryptRC4(plainText.toByteArray(), key.toByteArray())
                //binding.encryptedText.text = encryptedText
                // val encryptedText = RC4Algorithm.encryptRC4(plainText.toByteArray(Charsets.UTF_8), key.toByteArray(Charsets.UTF_8))
                //binding.encryptedText.text = Base64.encodeToString(encryptedText, Base64.DEFAULT)
                // Langsung memanggil encryptRC4
                val encryptedText = RC4Algorithm.encryptRC4(plainText, key)
                binding.encryptedText.text = encryptedText
            } catch (e: Exception){
                e.printStackTrace()
                binding.encryptedText.text = "Error: ${e.message}"
            }
        }

        binding.decryptButton.setOnClickListener {
            try {
                val cipherText = binding.inputText.text.toString()
                val key = binding.inputKey.text.toString()

                if (cipherText.isEmpty()) {
                    binding.decryptedText.text = getString(R.string.text_cannot_be_empty)
                    return@setOnClickListener
                }
                if (key.isEmpty()) {
                    binding.decryptedText.text = getString(R.string.key_cannot_be_empty)
                    return@setOnClickListener
                }

                //  val decryptedText = decryptRC4(Base64.decode(cipherText, Base64.DEFAULT), key.toByteArray())
                //binding.decryptedText.text = decryptedText
                //val decryptedText = RC4Algorithm.decryptRC4(Base64.decode(cipherText, Base64.DEFAULT), key.toByteArray(Charsets.UTF_8))
                //binding.decryptedText.text = String(decryptedText, Charsets.UTF_8)
                // Langsung memanggil decryptRC4
                val decryptedText = RC4Algorithm.decryptRC4(cipherText, key)
                binding.decryptedText.text = decryptedText
            } catch (e: Exception) {
                e.printStackTrace()
                binding.decryptedText.text = "Error: ${e.message}"
            }
        }

        binding.encryptedText.setOnClickListener {
            copyToClipboard(binding.encryptedText.text.toString(), "Encryption Result")
        }

        binding.decryptedText.setOnClickListener {
            copyToClipboard(binding.decryptedText.text.toString(), "Decryption Result")
        }
    }

    // Fungsi untuk menyalin teks ke clipboard
    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "$label copied to the clipboard", Toast.LENGTH_SHORT).show()
    }
}