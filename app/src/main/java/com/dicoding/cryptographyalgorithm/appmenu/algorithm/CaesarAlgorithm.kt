package com.dicoding.cryptographyalgorithm.appmenu.algorithm

object CaesarAlgorithm {
    // Fungsi Enkrpsi Caesar Cipher
    fun encryptCaesar(text: String, shift: Int): String {   // parameter `text` = teks yang ingin dienkripsi / didekripsi, parameter `shift` = ilai pergeseran (shift) untuk mengenkripsi huruf-huruf dalam teks.
        val result = StringBuilder()
        val shiftMod = shift % 26       //  agar nilai pergeseran tidak melebihi jumlah huruf dalam alfabet (26).

        for (char in text) {          // Perulangan untuk setiap karakter dalam teks
            if (char.isLetter()) {          // memeriksa apakah karakter adalah huruf
                val base = if (char.isLowerCase()) 'a' else 'A'     // menentukan apakah karakter huruf kecil atau besar
                val shiftedChar = (char + shiftMod - base).mod(26) + base.toInt()       // rumus untuk menghitung pergeseran karakter dalam alfabet
                result.append(shiftedChar.toChar())     // menambahkan karakter yang sudah digeser ke dalam string builder
            } else {
                result.append(char)         // menambahkan karakter yang bukan huruf ke dalam string builder
            }
        }

        return result.toString()            // mengembalikan string yang sudah dienkripsi atau didekripsi
    }

    // Fungsi dekripsi Caesar Cipher
    fun decryptCaesar(text: String, shift: Int): String {
        return encryptCaesar(text, -shift)  // membalik pergeseran function encryptCaesar untuk melakukan dekripsi (logic sama dikarenakan kunci adalah simetris)
    }
}