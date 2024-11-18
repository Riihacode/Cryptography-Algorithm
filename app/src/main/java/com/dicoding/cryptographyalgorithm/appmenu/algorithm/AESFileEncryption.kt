package com.dicoding.cryptographyalgorithm.appmenu.algorithm

import android.annotation.SuppressLint
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object AESFileEncryption {
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
    fun encryptData(inputData: ByteArray, secretKey: SecretKey): ByteArray {
        var encryptDataResult = ByteArray(0)
        try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            encryptDataResult = cipher.doFinal(inputData)

            Log.d("AESAlgorithm", "encryptData: Successfully encrypted data, size: ${encryptDataResult.size} bytes")
        } catch (e: Exception) {
            Log.e("AESAlgorithm", "encryptData Error: ${e.message}")
        }

        return encryptDataResult
    }

    @SuppressLint("GetInstance")
    fun decryptData(encryptedData: ByteArray, secretKey: SecretKey): ByteArray {
        var decryptDataResult = ByteArray(0)
        try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            decryptDataResult = cipher.doFinal(encryptedData)

            Log.d("AESAlgorithm", "decryptData: Successfully decrypted data, size: ${decryptDataResult.size} bytes")
        } catch (e: Exception) {
            Log.e("AESAlgorithm", "decryptData Error: ${e.message}")
        }

        return decryptDataResult
    }
}