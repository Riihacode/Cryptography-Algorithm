package com.dicoding.cryptographyalgorithm.appmenu.algorithm

object SuperEncryptionAlgorithm {
    fun encryptSuperEncryption(
        inputText: String,
        caesarShift: Int,
        railFenceRails: Int,
        rc4Key: String, // Menggunakan String untuk RC4 Key
        aesKeyString: String
    ): String {
        val caesarEncrypted = CaesarAlgorithm.encryptCaesar(inputText, caesarShift)
        val railFenceEncrypted = RailFenceAlgorithm.encryptRailFence(caesarEncrypted, railFenceRails)
        val rc4Encrypted = RC4Algorithm.encryptRC4(railFenceEncrypted, rc4Key)
        val aesKey = AESAlgorithm.generateAESKeyFromString(aesKeyString)
        return AESAlgorithm.encryptAES(rc4Encrypted, aesKey)
    }

    fun decryptSuperDecryption(
        encryptedText: String,
        caesarShift: Int,
        railFenceRails: Int,
        rc4Key: String,
        aesKeyString: String
    ): String {
        val aesKey = AESAlgorithm.generateAESKeyFromString(aesKeyString)
        val rc4Encrypted = AESAlgorithm.decryptAES(encryptedText, aesKey)
        val rc4Decrypted = RC4Algorithm.decryptRC4(rc4Encrypted, rc4Key)
        val railFenceDecrypted = RailFenceAlgorithm.decryptRailFence(rc4Decrypted, railFenceRails)

        return CaesarAlgorithm.decryptCaesar(railFenceDecrypted, caesarShift)
    }
}
