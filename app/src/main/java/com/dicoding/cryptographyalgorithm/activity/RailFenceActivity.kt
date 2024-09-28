package com.dicoding.cryptographyalgorithm.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.databinding.ActivityRailFenceBinding

class RailFenceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRailFenceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityRailFenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.encryptButton.setOnClickListener{
            val plainText = binding.inputText.text.toString()
            val rails = binding.inputKey.text.toString().toIntOrNull() ?: 0

            if(plainText.isNotEmpty() && rails > 1){
                val encryptedText = encrypt(plainText, rails)
                binding.outputText.text = getString(R.string.hasil_enkripsi)
                binding.outputText.append("\n$encryptedText")
            } else {
                binding.outputText.text = getString(R.string.invalid_input)
            }
        }

        binding.decryptButton.setOnClickListener{
            val cipherText = binding.inputText.text.toString()
            val rails = binding.inputKey.text.toString().toIntOrNull() ?: 0

            if(cipherText.isNotEmpty() && rails > 1){
                val decryptedText = decrypt(cipherText, rails)
                binding.decryptedText.text = getString(R.string.hasil_dekripsi)
                binding.decryptedText.append("\n$decryptedText")

            } else {
                binding.decryptedText.text = getString(R.string.invalid_input)
            }
        }
    }

    private fun encrypt(plainText: String, rails: Int): String {
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

    private fun decrypt(cipherText: String, rails: Int): String {
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