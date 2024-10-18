package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.RailFenceAlgorithm
import com.dicoding.cryptographyalgorithm.databinding.ActivityRailFenceBinding

class RailFenceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRailFenceBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRailFenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.encryptButton.setOnClickListener {
            val plainText = binding.inputText.text.toString()
            val keyRails = binding.inputKey.text.toString().toIntOrNull()

            if (plainText.isEmpty()) {
                binding.encryptedText.text = getString(R.string.text_cannot_be_empty)
                return@setOnClickListener
            }

            // Validasi apakah kunci valid
            if (keyRails == null || keyRails <= 1) {
                binding.encryptedText.text = getString(R.string.invalid_input_key)
                return@setOnClickListener
            }

            // Menghitung batasan maksimum berdasarkan memori yang tersedia
            val availableMemory = (Runtime.getRuntime().maxMemory() -
                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) / 1024 // in KB
            val maxRails = (availableMemory * 1024) / (plainText.length * 2) // Memori per rail dalam byte

            try {
                if (keyRails > maxRails) {
                    binding.encryptedText.text = "Key is out of maximum rails memory. Key must be between 2 until $maxRails (key recommendation is under 100 due to memory limit)."
                } else {
                    val encryptedText = RailFenceAlgorithm.encryptRailFence(plainText, keyRails)
                    binding.encryptedText.text = encryptedText
                }
            } catch (e: OutOfMemoryError) {
                binding.encryptedText.text = "Error: Out of memory. Memory limit: ${availableMemory}KB"
            } catch (e: Exception) {
                binding.encryptedText.text = "Unexpected error: ${e.message} (key recommendation is under 100 due to memory limit)."
            }
        }

        binding.decryptButton.setOnClickListener {
            val cipherText = binding.inputText.text.toString()
            val keyRails = binding.inputKey.text.toString().toIntOrNull()

            if (cipherText.isEmpty()) {
                binding.decryptedText.text = getString(R.string.text_cannot_be_empty)
                return@setOnClickListener
            }
            // Validasi apakah kunci valid
            if (keyRails == null || keyRails <= 1) {
                binding.decryptedText.text = getString(R.string.invalid_input_key)
                return@setOnClickListener
            }

            // Menghitung batasan maksimum berdasarkan memori yang tersedia
            val availableMemory = (Runtime.getRuntime().maxMemory() -
                    (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())) / 1024 // in KB
            val maxRails = (availableMemory * 1024) / (cipherText.length * 2) // Memori per rail dalam byte

            try {
                if (keyRails > maxRails) {
                    binding.decryptedText.text = "Key is out of maximum rails memory. Key must be between 2 until $maxRails (Key reccomendation is under 100 due to memory limit)."
                } else {
                    val decryptedText = RailFenceAlgorithm.decryptRailFence(cipherText, keyRails)
                    binding.decryptedText.text = decryptedText
                }
            } catch (e: OutOfMemoryError) {
                binding.decryptedText.text = "Error: Out of memory. Memory limit: ${availableMemory}KB"
            }  catch (e: Exception) {
                binding.encryptedText.text = "Unexpected error: ${e.message} (key recommendation is under 100 due to memory limit)."
            }
        }

        // Menambahkan listener untuk menyalin teks hasil enkripsi
        binding.encryptedText.setOnClickListener {
            copyToClipboard(binding.encryptedText.text.toString(), "Encryption Result")
        }

        // Menambahkan listener untuk menyalin teks hasil dekripsi
        binding.decryptedText.setOnClickListener {
            copyToClipboard(binding.decryptedText.text.toString(), "Decryption Result")
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