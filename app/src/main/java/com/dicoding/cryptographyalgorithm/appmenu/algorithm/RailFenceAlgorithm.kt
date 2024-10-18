package com.dicoding.cryptographyalgorithm.appmenu.algorithm

object RailFenceAlgorithm {
    fun encryptRailFence(plainText: String, rails: Int): String {
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

    fun decryptRailFence(cipherText: String, rails: Int): String {
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
}

