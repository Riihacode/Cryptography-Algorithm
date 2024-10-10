package com.dicoding.cryptographyalgorithm.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.databinding.ActivityCaesarBinding

class CaesarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaesarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityCaesarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.encryptButton.setOnClickListener{
            val text = binding.inputText.text.toString()
            val shift = binding.inputKey.text.toString().toIntOrNull() ?: 0
            val encrypted = caesarCipher(text, shift)
            binding.encryptedText.text = getString(R.string.hasil_enkripsi)
            binding.encryptedText.append("\n$encrypted")
        }

        binding.decryptButton.setOnClickListener{
            val text = binding.inputText.text.toString()
            val shift = binding.inputKey.text.toString().toIntOrNull() ?: 0
            val decrypted = caesarCipher(text, -shift)
            binding.decryptedText.text = getString(R.string.hasil_dekripsi)
            binding.decryptedText.append("\n$decrypted")
        }

        // Menambahkan listener untuk menyalin teks hasil enkripsi
        binding.encryptedText.setOnClickListener {
            copyToClipboard(binding.encryptedText.text.toString(), "Hasil Enkripsi")
        }

        // Menambahkan listener untuk menyalin teks hasil dekripsi
        binding.decryptedText.setOnClickListener {
            copyToClipboard(binding.decryptedText.text.toString(), "Hasil Dekripsi")
        }
    }

    private fun caesarCipher(text: String, shift: Int): String {
        val result = StringBuilder()
        val shiftMod = shift % 26

        for(char in text){
            if (char.isLetter()){
                val base = if(char.isLowerCase()) 'a' else 'A'
                val shiftedChar = (char + shiftMod - base).mod(26) + base.toInt()
                result.append(shiftedChar.toChar())
            } else {
                result.append(char)
            }
        }

        return result.toString()
    }

    // Fungsi untuk menyalin teks ke clipboard
    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        // Tampilkan toast untuk memberi tahu pengguna bahwa teks telah disalin
        Toast.makeText(this, "$label disalin ke clipboard", Toast.LENGTH_SHORT).show()
    }
}