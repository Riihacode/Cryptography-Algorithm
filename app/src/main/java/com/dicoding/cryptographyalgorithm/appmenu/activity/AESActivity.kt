package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.AESAlgorithm
import com.dicoding.cryptographyalgorithm.databinding.ActivityAesBinding
import javax.crypto.SecretKey

class AESActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAesBinding
    private lateinit var secretKey: SecretKey   // secretKey digunakan dalam algoritma AES untuk mengenkripsi dan mendekripsi data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol Enkripsi
        binding.encryptButton.setOnClickListener{
            val inputText = binding.inputText.text.toString()
            val keyText = binding.inputKey.text.toString()

            if(inputText.isNotEmpty() && keyText.isNotEmpty()) {
                try {
                    secretKey = AESAlgorithm.generateAESKeyFromString(keyText)
                    val encryptedText = AESAlgorithm.encryptAES(inputText, secretKey)
                    binding.encryptedText.text = encryptedText
                } catch (e: Exception) {
                    binding.encryptedText.text = "Error : ${e.message}"
                }
            } else {
                binding.encryptedText.text = getString(R.string.encryption_result)
            }
        }

        // Tombol dekripsi
        binding.decryptButton.setOnClickListener{
            val inputText = binding.inputText.text.toString()
            val keyText = binding.inputKey.text.toString()

            if(inputText.isNotEmpty() && keyText.isNotEmpty()){
                try {
                    secretKey = AESAlgorithm.generateAESKeyFromString(keyText)
                    val decryptedText = AESAlgorithm.decryptAES(inputText, secretKey)
                    binding.decryptedText.text = decryptedText
                } catch (e: Exception) {
                    binding.decryptedText.text = "Error : ${e.message}"
                }
            } else {
                binding.decryptedText.text = getString(R.string.decryption_result)
            }
        }

        // Menambahkan listener untuk menyalin teks hasil enkripsi
        binding.encryptedText.setOnClickListener {
            copyToClipboard(binding.encryptedText.text.toString(), "Encryption result")
        }

        // Menambahkan listener untuk menyalin teks hasil dekripsi
        binding.decryptedText.setOnClickListener {
            copyToClipboard(binding.decryptedText.text.toString(), "Decryption result")
        }
    }

    // Fungsi untuk menyalin teks ke clipboard
    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        // Tampilkan toast untuk memberi tahu pengguna bahwa teks telah disalin
        Toast.makeText(this, "$label copied to the clipboard", Toast.LENGTH_SHORT).show()
    }
}