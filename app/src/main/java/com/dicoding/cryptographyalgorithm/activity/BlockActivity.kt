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
import com.dicoding.cryptographyalgorithm.databinding.ActivityBlockBinding
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class BlockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlockBinding
    private lateinit var secretKey: SecretKey   // secretKey digunakan dalam algoritma AES untuk mengenkripsi dan mendekripsi data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol Enkripsi
        binding.encryptButton.setOnClickListener{
            val inputText = binding.inputText.text.toString()
            val keyText = binding.inputKey.text.toString()

            if(inputText.isNotEmpty() && keyText.isNotEmpty()) {
                try {
                    secretKey = generateAESKeyFromString(keyText)
                    val encryptedText = encryptAES(inputText, secretKey)
                    binding.encryptedText.text = getString(R.string.hasil_enkripsi)
                    binding.encryptedText.append("\n$encryptedText")
                } catch (e: Exception) {
                    binding.encryptedText.text = "Error : ${e.message}"
                }
            } else {
                binding.encryptedText.text = getString(R.string.hasil_enkripsi)
            }
        }

        // Tombol dekripsi
        binding.decryptButton.setOnClickListener{
            val inputText = binding.inputText.text.toString()
            val keyText = binding.inputKey.text.toString()

            if(inputText.isNotEmpty() && keyText.isNotEmpty()){
                try {
                    secretKey = generateAESKeyFromString(keyText)
                    val decryptedText = decryptAES(inputText, secretKey)
                    binding.decryptedText.text = getString(R.string.hasil_dekripsi)
                    binding.decryptedText.append("\n$decryptedText")
                } catch (e: Exception) {
                    binding.decryptedText.text = "Error : ${e.message}"
                }
            } else {
                binding.decryptedText.text = getString(R.string.hasil_dekripsi)
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

    // Fungsi untuk mengubah string menjadi array byte dan memastikan panjang kunci sesuai dengan standar AES yaitu 128-bit, 192-bit, atau 256-bit
    private fun generateAESKeyFromString(key: String): SecretKey {
        // Memastikan panjang kunci sesuai dengan AES
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val validKey: ByteArray = when (keyBytes.size) {
            16 -> keyBytes // 128-bit
            24 -> keyBytes // 192-bit
            32 -> keyBytes // 256-bit
            else -> throw IllegalArgumentException("Key length must be 16, 24, or 32 bytes long.")
        }

        return SecretKeySpec(validKey, "AES")   // SecretKeySpec untuk menghasilkan kunci AES yang valid dari array byte tersebut
    }

    // Fungsi untuk enkripsi menggunakan AES
    private fun encryptAES(inputText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")  // mendapatkan instance dari algoritma AES
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(inputText.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    //Fungsi untuk dekripsi menggunakan AES
    private fun decryptAES(inputText: String, secretKey: SecretKey): String{
        val cipher = Cipher.getInstance("AES") // mendapatkan instance dari algoritma AES
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(inputText, Base64.DEFAULT) // Base64 digunakan untuk mengubah data biner (byte array) menjadi format teks yang dapat dibaca oleh manusia
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
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