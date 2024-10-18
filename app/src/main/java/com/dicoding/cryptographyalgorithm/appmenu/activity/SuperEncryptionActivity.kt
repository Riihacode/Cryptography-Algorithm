package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.SuperEncryptionAlgorithm
//import com.dicoding.cryptographyalgorithm.appmenu.algorithm.SuperEncryptionAlgorithm
import com.dicoding.cryptographyalgorithm.databinding.ActivitySuperEncryptionBinding

class SuperEncryptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuperEncryptionBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuperEncryptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.encryptButton.setOnClickListener {
            val inputText = binding.inputText.text.toString()
            val key = binding.inputKey.text.toString()
            val caesarShift = 3
            val railFenceRails = 3
            val rc4Key = key

            if(inputText.isNotEmpty() && key.isNotEmpty()) {
                try {
                    val encryptedText =
                        SuperEncryptionAlgorithm.encryptSuperEncryption(
                            inputText,
                            caesarShift,
                            railFenceRails,
                            rc4Key,
                            key
                        )
                    binding.encryptedText.text = encryptedText
                } catch (e: Exception) {
                    binding.encryptedText.text = "Error: ${e.message}"
                }
            } else {
                binding.encryptedText.text = getString(R.string.input_both_please)
            }
        }

        binding.decryptButton.setOnClickListener {
            val encryptedText = binding.inputText.text.toString().removePrefix("Encrypted: ")
            val key = binding.inputKey.text.toString()
            val caesarShift = 3
            val railFenceRails = 3
            val rc4Key = key

            if(encryptedText.isNotEmpty() && key.isNotEmpty()) {
                try {
                    val decryptedText =
                        SuperEncryptionAlgorithm.decryptSuperDecryption(
                            encryptedText,
                            caesarShift,
                            railFenceRails,
                            rc4Key,
                            key
                        )
                    binding.decryptedText.text = decryptedText
                } catch (e: Exception) {
                    binding.decryptedText.text = "Error: ${e.message}"
                }
            } else {
                binding.decryptedText.text = getString(R.string.input_both_please)
            }
        }

        // Menambahkan listener untuk menyalin teks hasil enkripsi
        binding.encryptedText.setOnClickListener {
            copyToClipboard(binding.encryptedText.text.toString(), "Encryption Result")
        }

        // Menambahkan listener untuk menyalin teks hasil dekripsi
        binding.decryptedText.setOnClickListener {
            copyToClipboard(binding.decryptedText.text.toString(), "Encryption Result")
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