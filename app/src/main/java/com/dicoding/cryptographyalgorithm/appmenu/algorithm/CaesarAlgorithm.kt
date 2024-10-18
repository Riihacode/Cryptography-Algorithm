package com.dicoding.cryptographyalgorithm.appmenu.algorithm

object CaesarAlgorithm {
    fun encryptCaesar(text: String, shift: Int): String {
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

    fun decryptCaesar(text: String, shift: Int): String {
        return encryptCaesar(text, -shift)
    }
}