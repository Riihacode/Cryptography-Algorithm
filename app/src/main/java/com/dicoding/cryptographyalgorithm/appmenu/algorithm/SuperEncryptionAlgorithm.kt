package com.dicoding.cryptographyalgorithm.appmenu.algorithm

import android.util.Log
import javax.crypto.spec.SecretKeySpec

object SuperEncryptionAlgorithm {
    fun encryptSuperEncryption(
        inputText: String,
        caesarShift: Int,
        railFenceRails: Int,
        rc4Key: String,
        aesKeyString: String
    ): String {
        val caesarEncrypted = CaesarAlgorithm.encryptCaesar(inputText, caesarShift)
        Log.d("SuperEncryption", "caesarEncrypted = $caesarEncrypted")

        val railFenceEncrypted = RailFenceAlgorithm.encryptRailFence(caesarEncrypted, railFenceRails)
        Log.d("SuperEncryption", "railFenceEncrypted = $railFenceEncrypted")

        val rc4Encrypted = RC4Algorithm.encryptRC4(railFenceEncrypted, rc4Key)
        Log.d("SuperEncryption", "rc4Encrypted = $rc4Encrypted")

        val aesKey = AESAlgorithm.generateAESKeyFromString(aesKeyString)
        // Log untuk menampilkan kunci AES dalam format heksadesimal
        val keyHex = (aesKey as SecretKeySpec).encoded.joinToString("") { String.format("%02x", it) }
        Log.d("SuperEncryption", "aesKey (hex) = $keyHex")

        val aesEncrypted = AESAlgorithm.encryptAES(rc4Encrypted, aesKey)
        Log.d("SuperEncryption", "aesEncrypted = $aesEncrypted")

        val superEncryptionEncryptedResult = aesEncrypted
        Log.d("SuperEncryption", "superEncryptionEncryptedResult = $superEncryptionEncryptedResult")

        return superEncryptionEncryptedResult
    }

    fun decryptSuperDecryption(
        encryptedText: String,
        caesarShift: Int,
        railFenceRails: Int,
        rc4Key: String,
        aesKeyString: String
    ): String {
        val aesKey = AESAlgorithm.generateAESKeyFromString(aesKeyString)
        Log.d("SuperEncryption", "aesKey = $aesKey")
        val keyHex = (aesKey as SecretKeySpec).encoded.joinToString("") { String.format("%02x", it) }
        Log.d("SuperEncryption", "aesKey (hex) = $keyHex")

        val aesDecrypted = AESAlgorithm.decryptAES(encryptedText, aesKey)
        Log.d("SuperEncryption", "aesDecrypted = $aesDecrypted")

        val rc4Decrypted = RC4Algorithm.decryptRC4(aesDecrypted, rc4Key)
        Log.d("SuperEncryption", "rc4Decrypted = $rc4Decrypted")

        val railFenceDecrypted = RailFenceAlgorithm.decryptRailFence(rc4Decrypted, railFenceRails)
        Log.d("SuperEncryption", "railFenceDecrypted = $railFenceDecrypted")

        val caesarDecrypted = CaesarAlgorithm.decryptCaesar(railFenceDecrypted, caesarShift)
        Log.d("SuperEncryption", "caesarDecrypted = $caesarDecrypted")

        val superEncryptionDecryptedResult = caesarDecrypted
        Log.d("SuperEncryption", "superEncryptionDecryptedResult = $superEncryptionDecryptedResult")

        return superEncryptionDecryptedResult
    }
}
