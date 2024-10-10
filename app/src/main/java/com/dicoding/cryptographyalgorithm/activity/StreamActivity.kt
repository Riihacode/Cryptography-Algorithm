package com.dicoding.cryptographyalgorithm.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.*
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.databinding.ActivityStreamBinding

class StreamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStreamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.encryptButton.setOnClickListener {
            val plainText = binding.inputText.text.toString()
            val key = binding.inputKey.text.toString()

            try {
                // Validasi apakah input text atau key kosong
                if (plainText.isEmpty()) {
                    binding.encryptedText.text = getString(R.string.text_cannot_be_empty)
                    return@setOnClickListener
                }
                if (key.isEmpty()) {
                    binding.encryptedText.text = getString(R.string.key_cannot_be_empty)
                    return@setOnClickListener
                }

                val encryptedText = encryptRC4(plainText.toByteArray(), key.toByteArray())
                binding.encryptedText.text = getString(R.string.hasil_enkripsi)
                binding.encryptedText.append("\n$encryptedText")
            } catch (e: Exception){
                e.printStackTrace()
                binding.encryptedText.text = "Error: ${e.message}"
            }
        }

        binding.decryptButton.setOnClickListener {
            try {
                val cipherText = binding.inputText.text.toString()
                val key = binding.inputKey.text.toString()

                // Validasi apakah input text atau key kosong
                if (cipherText.isEmpty()) {
                    binding.decryptedText.text = getString(R.string.text_cannot_be_empty)
                    return@setOnClickListener
                }
                if (key.isEmpty()) {
                    binding.decryptedText.text = getString(R.string.key_cannot_be_empty)
                    return@setOnClickListener
                }

                val decryptedText = decryptRC4(Base64.decode(cipherText, Base64.DEFAULT), key.toByteArray())
                binding.decryptedText.text = getString(R.string.hasil_dekripsi)
                binding.decryptedText.append("\n$decryptedText")
            } catch (e: Exception) {
                e.printStackTrace()
                binding.decryptedText.text = "Error: ${e.message}"
            }
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

    private fun encryptRC4(inputText: ByteArray, key: ByteArray): String {
        val s = ByteArray(256)
        var j = 0

        //Kunci inisialisasi
        for(i in 0 until 256){
            s[i] = i.toByte()
        }

        for(i in 0 until 256){
            j = (j + s[i] + key[i % key.size].toInt()) and 0xFF
            swap(s, i, j)
        }

        //Enkripsi
        val output = ByteArray(inputText.size)
        var i = 0
        j = 0

        for(k in inputText.indices){
            i = (i + 1) and 0xFF
            j = (j + s[i].toInt()) and 0xFF
            swap(s, i, j)

            val index = (s[i] + s[j]).toInt() and 0xFF
            output[k] = (inputText[k].toInt() xor s[index].toInt()).toByte()
        }

        return Base64.encodeToString(output, Base64.DEFAULT)
    }

    private fun decryptRC4(inputText: ByteArray, key: ByteArray): String{
        val s = ByteArray(256)
        var j = 0

        //kunci inisialisasi
        for(i in 0 until 256){
            s[i] = i.toByte()
        }

        for(i in 0 until 256){
            j = (j + s[i] + key[i % key.size].toInt()) and 0xFF
            swap(s, i, j)
        }

        //Dekripsi
        val output = ByteArray(inputText.size)
        var i = 0
        j = 0

        for(k in inputText.indices){
            i = (i + 1) and 0xFF
            j = (j + s[i].toInt()) and 0xFF
            swap(s, i, j)

            val index = (s[i] + s[j]).toInt() and 0xFF
            output[k] = (inputText[k].toInt() xor s[index].toInt()).toByte()
        }

        return String(output)
    }

    private fun swap(s: ByteArray, i: Int, j:Int){
        val temp = s[i]
        s[i] = s[j]
        s[j] = temp
    }

    // Fungsi untuk menyalin teks ke clipboard
    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        // Tampilkan toast untuk memberi tahu pengguna bahwa teks telah disalin
        Toast.makeText(this, "$label disalin ke clipboard", Toast.LENGTH_SHORT).show()
    }
}