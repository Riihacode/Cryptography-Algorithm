package com.dicoding.cryptographyalgorithm.activity

import android.os.Bundle
import android.util.Base64
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
            val inputText = binding.inputText.text.toString()
            val key = binding.inputKey.text.toString()

            // Validasi apakah input text atau key kosong
            if (inputText.isEmpty()) {
                binding.outputText.text = "Error: Input text tidak boleh kosong"
                return@setOnClickListener
            }
            if (key.isEmpty()) {
                binding.outputText.text = "Error: Key tidak boleh kosong"
                return@setOnClickListener
            }

            val encryptedText = rc4Encrypt(inputText.toByteArray(), key.toByteArray())
            binding.outputText.text = getString(R.string.hasil_enkripsi)
            binding.outputText.append("\n$encryptedText")
        }

        binding.decryptButton.setOnClickListener {
            try {
                val inputText = binding.inputText.text.toString()
                val key = binding.inputKey.text.toString()

                // Validasi apakah input text atau key kosong
                if (inputText.isEmpty()) {
                    binding.decryptedText.text = "Error: Input text tidak boleh kosong"
                    return@setOnClickListener
                }
                if (key.isEmpty()) {
                    binding.decryptedText.text = "Error: Key tidak boleh kosong"
                    return@setOnClickListener
                }

                val decryptedText = rc4Decrypt(Base64.decode(inputText, Base64.DEFAULT), key.toByteArray())
                binding.decryptedText.text = getString(R.string.hasil_dekripsi)
                binding.decryptedText.append("\n$decryptedText")
            } catch (e: Exception) {
                e.printStackTrace()
                binding.decryptedText.text = "Error: ${e.message}"
            }
        }

    }

    private fun rc4Encrypt(inputText: ByteArray, key: ByteArray): String {
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

    private fun rc4Decrypt(inputText: ByteArray, key: ByteArray): String{
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
}