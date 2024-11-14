package com.dicoding.cryptographyalgorithm.appmenu.algorithm

import android.util.Base64
import android.util.Log
import java.util.Arrays

object RC4Algorithm {
    fun encryptRC4(inputText: String, key: String): String {
        val inputBytes = inputText.toByteArray(Charsets.UTF_8)      // Konversi inputText ke ByteArray
        val keyBytes = key.toByteArray(Charsets.UTF_8)              // Konversi key ke ByteArray. Contoh keynya adalah `KEY` maka `keyBytes = [75, 69, 89]`
        val s = ByteArray(256)                                           // `inisialisasi array S` untuk kunci inisialisasi
        var j = 0                                                             // inisialisasi index j untuk kunci inisialisasi

        // Kunci inisialisasi (inisialisasi array S ). Contoh nilai awal s adalah s = [0, 1, 2, 3, ..., 255]
        for (i in 0 until 256) {        // looping nilai i dari 0 sampai 256
            s[i] = i.toByte()
        }

        // Key Scheduling Algorith (KSA). Digunakan untuk mengacak nilai dalam `array s` menggunakan kunci/key
        for (i in 0 until 256) {        // looping nilai i dari 0 sampai 256
            j = (j + s[i] + keyBytes[i % keyBytes.size].toInt()) and 0xFF     // j dihitung menggunakan rumus ini. `keyBytes[i % keyBytes.size]` digunakan untuk menentukan posisi index array dari `val keyBytes = key.toByteArray(Charsets.UTF_8)`. Contoh `j = (0 + s[0] + keyBytes[0 % 3].toInt()) & 0xFF` di sini keyBytes[0] adalah 75
            swap(s, i, j)

            // Log untuk melihat proses KSA
            // Log.d("RC4", "KSA - Iterasi $i: keyByte = ${(keyBytes[i % keyBytes.size].toInt() and 0xFF)}, j = $j, S[$i] = ${(s[i].toInt() and 0xFF)}, S[$j] = ${(s[j].toInt() and 0xFF)}")
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
            Log.d("index","index = ${(s[i] + s[j]).toInt() and 0xFF}") // WORK
            output[k] = (inputBytes[k].toInt() xor s[index].toInt()).toByte()

            // Log untuk melihat proses PRGA dan hasil XOR
            Log.d("RC4", "PRGA - Byte ke-$k: inputByte = ${(inputBytes[k].toInt() and 0xFF)}, keystreamByte = ${(s[index].toInt() and 0xFF)}, XOR = ${(output[k].toInt() and 0xFF)}")
        }

        // Log hasil sebelum Base64
        Log.d("RC4", "Hasil sebelum Base64: ${output.joinToString(", ") { (it.toInt() and 0xFF).toString() }}")

        // Hasil diencode ke dalam Base64
        val encryptedBase64Output = Base64.encodeToString(output, Base64.DEFAULT)

        // Log hasil setelah Base64
        Log.d("RC4", "Hasil setelah Base64: $encryptedBase64Output")

        return encryptedBase64Output
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

            // Log untuk melihat proses KSA, termasuk nilai keyBytes[i % keyBytes.size]
            //Log.d("RC4", "KSA - Iterasi $i: j = $j, S[$i] = ${(s[i].toInt() and 0xFF)}, S[$j] = ${(s[j].toInt() and 0xFF)}, keyByte = ${(keyBytes[i % keyBytes.size].toInt() and 0xFF)}")
        }

        // Enkripsi & Dekripsi (karena RC4 simetris, logika enkripsi sama dengan dekripsi)
        val output = ByteArray(inputBytes.size)
        var i = 0
        j = 0

        for (k in inputBytes.indices) {
            i = (i + 1) and 0xFF
            Log.d("j", "j = ${j and 0xFF}")
            Log.d("i", "i = ${i and 0xFF}")
            j = (j + s[i].toInt()) and 0xFF
            swap(s, i, j)

            val index = (s[i] + s[j]).toInt() and 0xFF
            Log.d("index","index = ${(s[i] + s[j]).toInt() and 0xFF}") // WORK
            output[k] = (inputBytes[k].toInt() xor s[index].toInt()).toByte()

            // Log untuk melihat setiap byte dalam PRGA
            Log.d("RC4", "PRGA - Byte ke-$k: inputByte = ${(inputBytes[k].toInt() and 0xFF)}, keystreamByte = ${(s[index].toInt() and 0xFF)}, XOR = ${(output[k].toInt() and 0xFF)}")

        }

        // Log hasil sebelum Base64 decoding (masih berupa ByteArray)
        Log.d("RC4", "Hasil sebelum decoding Base64 (ByteArray): ${Arrays.toString(output)}")

        // Konversi hasil dekripsi dari ByteArray ke String (UTF-8)
        val decryptedBase64Output = String(output, Charsets.UTF_8)

        // Log untuk melihat hasil dekripsi sebelum dikembalikan
        Log.d("RC4", "Hasil dekripsi (setelah Base64 decoding): $decryptedBase64Output")

        return decryptedBase64Output
    }

    private fun swap(s: ByteArray, i: Int, j: Int) {
        val temp = s[i]
        Log.d("RC4", "Swapping: S[i] = S[$i] = ${(s[i].toInt() and 0xFF)}, S[j] = S[$j] = ${(s[j].toInt() and 0xFF)}, temp = ${(temp.toInt() and 0xFF)}")
        s[i] = s[j]
        s[j] = temp
    }
}