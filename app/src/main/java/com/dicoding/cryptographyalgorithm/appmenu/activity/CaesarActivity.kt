package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.CaesarAlgorithm
import com.dicoding.cryptographyalgorithm.databinding.ActivityCaesarBinding

class CaesarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaesarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaesarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.encryptButton.setOnClickListener{
            val text = binding.inputText.text.toString()
            val inputKey = binding.inputKey.text.toString()

            if(inputKey.isEmpty() || text.isEmpty()){
                binding.encryptedText.text = getString(R.string.text_and_key_cannot_be_empty)
            } else {
                if(isKeyValid(inputKey)){
                    val shift = inputKey.toInt()
                    val encrypted = CaesarAlgorithm.encryptCaesar(text, shift)
                    binding.encryptedText.text = encrypted
                } else {
                    binding.encryptedText.text = getString(R.string.key_cannot_be_more_than_214748367)
                }
            }
        }

        binding.decryptButton.setOnClickListener{
            val text = binding.inputText.text.toString()
            val inputKey = binding.inputKey.text.toString()
            if (inputKey.isEmpty() || inputKey.isEmpty()){
                binding.decryptedText.text = getString(R.string.text_and_key_cannot_be_empty)
            } else {
                if(isKeyValid(inputKey)){
                    val shift = inputKey.toInt()
                    val decrypted = CaesarAlgorithm.decryptCaesar(text, shift)
                    binding.decryptedText.text = decrypted
                } else {
                    binding.decryptedText.text = getString(R.string.key_cannot_be_more_than_214748367)
                }
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

    private fun isKeyValid(keyInput: String): Boolean {
        return try {
            val key = keyInput.toLong()
            key in Int.MIN_VALUE..Int.MAX_VALUE
        } catch (e: NumberFormatException) {
            false
        }
    }

    // Fungsi untuk menyalin teks ke clipboard
    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        // Tampilkan toast untuk memberi tahu pengguna bahwa teks telah disalin
        Toast.makeText(this, "$label copied to the clipboard", Toast.LENGTH_SHORT).show()
    }
}