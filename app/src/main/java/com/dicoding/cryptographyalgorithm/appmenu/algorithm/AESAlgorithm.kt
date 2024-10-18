package com.dicoding.cryptographyalgorithm.appmenu.algorithm

import android.annotation.SuppressLint
import android.util.Base64
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

        return SecretKeySpec(validKey, "AES")
    }

    @SuppressLint("GetInstance")
    fun encryptAES(inputText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(inputText.toByteArray(Charsets.UTF_8))

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    @SuppressLint("GetInstance")
    fun decryptAES(inputText: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(inputText, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(decodedBytes)

        return String(decryptedBytes, Charsets.UTF_8)
    }
}