package com.dicoding.cryptographyalgorithm.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.databinding.ActivityRailFenceBinding

class RailFenceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRailFenceBinding
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
                    val encryptedText = encryptRailFence(plainText, keyRails)
                    binding.encryptedText.text = getString(R.string.hasil_enkripsi)
                    binding.encryptedText.append("\n$encryptedText")
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
                    binding.decryptedText.text =
                        "Key is out of maximum rails memory. Key must be between 2 until $maxRails (Key reccomendation is under 100 due to memory limit)."
                } else {
                    val decryptedText = decryptRailFence(cipherText, keyRails)
                    binding.decryptedText.text = getString(R.string.hasil_dekripsi)
                    binding.decryptedText.append("\n$decryptedText")
                }
            } catch (e: OutOfMemoryError) {
                binding.decryptedText.text = "Error: Out of memory. Memory limit: ${availableMemory}KB"
            }  catch (e: Exception) {
                binding.encryptedText.text = "Unexpected error: ${e.message} (key recommendation is under 100 due to memory limit)."
            }
        }
    }

    private fun encryptRailFence(plainText: String, rails: Int): String {
        if(rails <= 1) return plainText

        val fence = Array(rails) { StringBuilder() }
        var rail = 0
        var direction = 1 // 1 means moving down, -1 means moving up

        for(char in plainText){
            fence[rail].append(char)
            rail += direction

            if(rail == rails -1){
                direction = -1
            } else if(rail == 0){
                direction = 1
            }
        }

        return fence.joinToString(""){ it.toString() }
    }

    private fun decryptRailFence(cipherText: String, rails: Int): String {
        if(rails <= 1 ) return cipherText

        val length = cipherText.length
        val fence = Array(rails) { CharArray(length) }
        var rail = 0
        var direction = 1

        // Fill the zigzag pattern with placeholders
        for(i in 0 until length) {
            fence[rail][i] = '*'
            rail += direction
            if (rail == rails - 1){
                direction = -1
            } else if (rail == 0) {
                direction = 1
            }
        }

        // Place the ciphertext characters into the pattern
        var index = 0
        for (r in 0 until rails){
            for (i in 0 until length){
                if(fence[r][i] == '*' && index < cipherText.length){
                    fence[r][i] = cipherText[index]
                    index++
                }
            }
        }

        // Now read the text in zigzag order
        rail = 0
        direction = 1
        val decryptedText = StringBuilder()

        for(i in 0 until length){
            decryptedText.append(fence[rail][i])
            rail += direction
            if(rail == rails - 1){
                direction = -1
            } else if (rail ==0){
                direction = 1
            }
        }

        return decryptedText.toString()
    }
}