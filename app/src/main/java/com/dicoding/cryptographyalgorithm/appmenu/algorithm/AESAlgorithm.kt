package com.dicoding.cryptographyalgorithm.appmenu.algorithm

import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object AESAlgorithm {
    fun generateAESKeyFromString(key: String): SecretKey {
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val validKey: ByteArray = when (keyBytes.size) {
            16 -> keyBytes
            24 -> keyBytes
            32 -> keyBytes
            else -> throw IllegalArgumentException("Key length must be 16, 24, or 32 bytes long.")
        }

        val secretKeySpec = SecretKeySpec(validKey, "AES")

        val keyHex = validKey.joinToString("") { String.format("%02x", it) }
        Log.d("AESAlgorithm", "generateAESKeyFromString: Key (hex): $keyHex")

        return secretKeySpec
    }

    @SuppressLint("GetInstance")
    fun encryptAES(inputText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(inputText.toByteArray(Charsets.UTF_8))

        val encryptAESResult = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        Log.d("AESAlgorithm", "encryptAESResult: $encryptAESResult")

        return encryptAESResult
    }

    @SuppressLint("GetInstance")
    fun decryptAES(inputText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(inputText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)

        val decryptAESResult = String(decryptedBytes, Charsets.UTF_8)
        Log.d("AESAlgorithm", "decryptAESResult: $decryptAESResult")

        return decryptAESResult
    }
}