package com.dicoding.cryptographyalgorithm.appmenu.algorithm

import android.util.Base64

object RC4Algorithm {
    //private fun encryptRC4(inputText: ByteArray, key: ByteArray): String {
    /*
    fun encryptRC4(inputText: ByteArray, key: ByteArray): ByteArray {
        val s = ByteArray(256)
        var j = 0

        //Kunci inisialisasi
        for (i in 0 until 256) {
            s[i] = i.toByte()
        }

        for (i in 0 until 256) {
            j = (j + s[i] + key[i % key.size].toInt()) and 0xFF
            swap(s, i, j)
        }


        // Enkripsi & Dekripsi
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

        //return Base64.encodeToString(output, Base64.DEFAULT)
        //return String(output)
        return output
    }

    fun decryptRC4(inputText: ByteArray, key: ByteArray): ByteArray {
        return encryptRC4(inputText, key)  // Logika decrypt RC4 sama seperti encrypt
    }
    */
    fun encryptRC4(inputText: String, key: String): String {
        val inputBytes = inputText.toByteArray(Charsets.UTF_8)
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val s = ByteArray(256)
        var j = 0

        // Kunci inisialisasi
        for (i in 0 until 256) {
            s[i] = i.toByte()
        }

        for (i in 0 until 256) {
            j = (j + s[i] + keyBytes[i % keyBytes.size].toInt()) and 0xFF
            swap(s, i, j)
        }

        // Enkripsi & Dekripsi
        val output = ByteArray(inputBytes.size)
        var i = 0
        j = 0

        for (k in inputBytes.indices) {
            i = (i + 1) and 0xFF
            j = (j + s[i].toInt()) and 0xFF
            swap(s, i, j)

            val index = (s[i] + s[j]).toInt() and 0xFF
            output[k] = (inputBytes[k].toInt() xor s[index].toInt()).toByte()
        }

        // Hasil diencode ke dalam Base64
        return Base64.encodeToString(output, Base64.DEFAULT)
    }

    fun decryptRC4(inputText: String, key: String): String {
        // Decode inputText dari Base64
        val inputBytes = Base64.decode(inputText, Base64.DEFAULT)
        val keyBytes = key.toByteArray(Charsets.UTF_8)
        val s = ByteArray(256)
        var j = 0

        // Kunci inisialisasi
        for (i in 0 until 256) {
            s[i] = i.toByte()
        }

        for (i in 0 until 256) {
            j = (j + s[i] + keyBytes[i % keyBytes.size].toInt()) and 0xFF
            swap(s, i, j)
        }

        // Enkripsi & Dekripsi (karena RC4 simetris, logika enkripsi sama dengan dekripsi)
        val output = ByteArray(inputBytes.size)
        var i = 0
        j = 0

        for (k in inputBytes.indices) {
            i = (i + 1) and 0xFF
            j = (j + s[i].toInt()) and 0xFF
            swap(s, i, j)

            val index = (s[i] + s[j]).toInt() and 0xFF
            output[k] = (inputBytes[k].toInt() xor s[index].toInt()).toByte()
        }

        // Konversi hasil dekripsi dari ByteArray ke String (UTF-8)
        return String(output, Charsets.UTF_8)
    }

    private fun swap(s: ByteArray, i: Int, j: Int) {
        val temp = s[i]
        s[i] = s[j]
        s[j] = temp
    }
}