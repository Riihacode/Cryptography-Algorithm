package com.dicoding.cryptographyalgorithm.activity

import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.databinding.ActivitySuperBinding
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class SuperActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySuperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuperBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.encryptButton.setOnClickListener {
            val inputText = binding.inputText.text.toString()
            val key = binding.inputKey.text.toString()
            val caesarShift = 3
            val railFenceRails = 3
            val rc4Key = key.toByteArray()
            val aesKeyString = key

            if(inputText.isNotEmpty() && key.isNotEmpty()) {
                try {
                    val encryptedText =
                        superEncrypt(inputText, caesarShift, railFenceRails, rc4Key, aesKeyString)
                    binding.encryptedText.text = "Encrypted: $encryptedText"
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
            val rc4Key = key.toByteArray()
            val aesKeyString = key

            if(encryptedText.isNotEmpty() && key.isNotEmpty()) {
                try {
                    val decryptedText =
                        superDecrypt(
                            encryptedText,
                            caesarShift,
                            railFenceRails,
                            rc4Key,
                            aesKeyString
                        )
                    binding.decryptedText.text = "Decrypted: $decryptedText"
                } catch (e: Exception) {
                    binding.decryptedText.text = "Error: ${e.message}"
                }
            } else {
                binding.decryptedText.text = getString(R.string.input_both_please)
            }
        }
    }

    private fun superEncrypt(
        inputText: String,
        caesarShift: Int,
        railFenceRails: Int,
        rc4Key: ByteArray,
        aesKeyString: String
    ): String {
        val caesarEncrypted = caesarCipher(inputText, caesarShift)
        val railFenceEncrypted = encryptRailFence(caesarEncrypted, railFenceRails)
        val rc4Encrypted = encryptRC4(railFenceEncrypted.toByteArray(), rc4Key)
        val aesKey = generateAESKeyFromString(aesKeyString)
        return encryptAES(rc4Encrypted, aesKey)
    }

    private fun superDecrypt(
        encryptedText: String,
        caesarShift: Int,
        railFenceRails: Int,
        rc4Key: ByteArray,
        aesKeyString: String
    ): String {
        val aesKey = generateAESKeyFromString(aesKeyString)
        val rc4Encrypted = decryptAES(encryptedText, aesKey)
        val rc4Decrypted = decryptRC4(Base64.decode(rc4Encrypted, Base64.DEFAULT), rc4Key)
        val railFenceDecrypted = decryptRailFence(rc4Decrypted, railFenceRails)
        return caesarCipher(railFenceDecrypted, -caesarShift)
    }

    private fun caesarCipher(text: String, shift: Int): String {
        val result = StringBuilder()
        val shiftMod = shift % 26

        for (char in text) {
            if (char.isLetter()) {
                val base = if (char.isLowerCase()) 'a' else 'A'
                val shiftedChar = (char + shiftMod - base).mod(26) + base.toInt()
                result.append(shiftedChar.toChar())
            } else {
                result.append(char)
            }
        }

        return result.toString()
    }

    private fun encryptRailFence(plainText: String, rails: Int): String {
        if (rails <= 1) return plainText

        val fence = Array(rails) { StringBuilder() }
        var rail = 0
        var direction = 1

        for (char in plainText) {
            fence[rail].append(char)
            rail += direction

            if (rail == rails - 1) {
                direction = -1
            } else if (rail == 0) {
                direction = 1
            }
        }

        return fence.joinToString("") { it.toString() }
    }

    private fun decryptRailFence(cipherText: String, rails: Int): String {
        if (rails <= 1) return cipherText

        val length = cipherText.length
        val fence = Array(rails) { CharArray(length) }
        var rail = 0
        var direction = 1

        for (i in 0 until length) {
            fence[rail][i] = '*'
            rail += direction
            if (rail == rails - 1) {
                direction = -1
            } else if (rail == 0) {
                direction = 1
            }
        }

        var index = 0
        for (r in 0 until rails) {
            for (i in 0 until length) {
                if (fence[r][i] == '*' && index < cipherText.length) {
                    fence[r][i] = cipherText[index]
                    index++
                }
            }
        }

        rail = 0
        direction = 1
        val decryptedText = StringBuilder()

        for (i in 0 until length) {
            decryptedText.append(fence[rail][i])
            rail += direction
            if (rail == rails - 1) {
                direction = -1
            } else if (rail == 0) {
                direction = 1
            }
        }

        return decryptedText.toString()
    }

    private fun encryptRC4(inputText: ByteArray, key: ByteArray): String {
        val s = ByteArray(256)
        var j = 0

        for (i in 0 until 256) {
            s[i] = i.toByte()
        }

        for (i in 0 until 256) {
            j = (j + s[i] + key[i % key.size].toInt()) and 0xFF
            swap(s, i, j)
        }

        val output = ByteArray(inputText.size)
        var i = 0
        j = 0

        for (k in inputText.indices) {
            i = (i + 1) and 0xFF
            j = (j + s[i].toInt()) and 0xFF
            swap(s, i, j)

            val index = (s[i] + s[j]).toInt() and 0xFF
            output[k] = (inputText[k].toInt() xor s[index].toInt()).toByte()
        }

        return Base64.encodeToString(output, Base64.DEFAULT)
    }

    private fun decryptRC4(inputText: ByteArray, key: ByteArray): String {
        val s = ByteArray(256)
        var j = 0

        for (i in 0 until 256) {
            s[i] = i.toByte()
        }

        for (i in 0 until 256) {
            j = (j + s[i] + key[i % key.size].toInt()) and 0xFF
            swap(s, i, j)
        }

        val output = ByteArray(inputText.size)
        var i = 0
        j = 0

        for (k in inputText.indices) {
            i = (i + 1) and 0xFF
            j = (j + s[i].toInt()) and 0xFF
            swap(s, i, j)

            val index = (s[i] + s[j]).toInt() and 0xFF
            output[k] = (inputText[k].toInt() xor s[index].toInt()).toByte()
        }

        return String(output)
    }

    private fun swap(s: ByteArray, i: Int, j: Int) {
        val temp = s[i]
        s[i] = s[j]
        s[j] = temp
    }

    private fun generateAESKeyFromString(key: String): SecretKey {
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val validKey: ByteArray = when (keyBytes.size) {
            16 -> keyBytes
            24 -> keyBytes
            32 -> keyBytes
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

    private fun decryptAES(inputText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(inputText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}