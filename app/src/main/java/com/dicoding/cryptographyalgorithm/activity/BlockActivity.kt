package com.dicoding.cryptographyalgorithm.activity

import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.databinding.ActivityBlockBinding
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class BlockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBlockBinding
    private lateinit var secretKey: SecretKey

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
                    binding.outputText.text = getString(R.string.hasil_enkripsi)
                    binding.outputText.append("\n$encryptedText")
                } catch (e: Exception) {
                    binding.outputText.text = "Error : ${e.message}"
                }
            } else {
                binding.outputText.text = getString(R.string.hasil_enkripsi)
            }
        }

        // dekripsi
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
    }

    private fun generateAESKeyFromString(key: String): SecretKey {
        // Memastikan panjang kunci sesuai dengan AES
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val validKey: ByteArray = when (keyBytes.size) {
            16 -> keyBytes // 128-bit
            24 -> keyBytes // 192-bit
            32 -> keyBytes // 256-bit
            else -> throw IllegalArgumentException("Key length must be 16, 24, or 32 bytes long.")
        }

        return SecretKeySpec(validKey, "AES")
    }

    private fun encryptAES(inputText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(inputText.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    private fun decryptAES(inputText: String, secretKey: SecretKey): String{
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(inputText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}