package com.dicoding.cryptographyalgorithm.appmenu.algorithm

object RailFenceAlgorithm {
    // Fungsi Enkripsi Rail Fence
    fun encryptRailFence(plainText: String, rails: Int): String {   // parameter `plaintext` = teks yang akan dienkripsi; parameter `rails` = jumlah baris rail fence
        if (rails <= 1) return plainText        // validasi jika rails lebih kecil atau sama dengan 1, fungsi langsung mengembalikan teks asli tanpa enkripsi (baris harus >1)

        val fence = Array(rails) { StringBuilder() }    // fence adalah array berisi StringBuilder sebanyak jumlah rails. Setiap StringBuilder mewakili sebuah baris (rail).
        var rail = 0                            // inisialisasi baris yang sedang diproses. Mulai dari baris pertama (0).
        var direction = 1                       // inisialisasi arah yang sedang diproses. Mulai dari atas ke bawah (1)

        for (char in plainText) {         // looping untuk setiap karakter dalam teks yang akan dienkripsi
            fence[rail].append(char)            // menambahkan karakter ke baris saat ini dalam variabel `fence`
            rail += direction                   // mengatur pergerakan posisi karakter dalam pola zig-zag. (rail = rail + direction)

            // memeriksa apakah sudah mencapai batas paling bawah atau paling atas dari rail fence
            if (rail == rails - 1) {
                direction = -1                // nanti diproses pada line `rail += direction` (rail = rail + (1-1))
            } else if (rail == 0) {
                direction = 1                 // nanti diproses pada line `rail += direction` (rail = railt + (1+1))
            }
        }

        return fence.joinToString("") { it.toString() }     // menggabungkan semua baris dalam fence menjadi satu string
    }

    // Fungsi untuk dekripsi Rail Fence
    fun decryptRailFence(cipherText: String, rails: Int): String {      // parameter `cipherText` = teks yang akan didekripsi; parameter `rails` = jumlah baris rail fence
        if (rails <= 1) return cipherText      // validasi jika rails lebih kecil atau sama dengan 1, fungsi langsung mengembalikan teks asli tanpa dekripsi (baris harus >1)

        val length = cipherText.length     // inisialisasi panjang teks yang akan didekripsi
        val fence = Array(rails) { CharArray(length) }      // fence adalah array dua dimensi yang berfungsi untuk menyimpan pola zig-zag dari teks yang akan didekripsi.
        var rail = 0                        // inisialisasi baris yang sedang diproses. Mulai dari baris pertama (0).
        var direction = 1                   // inisialisasi arah yang sedang diproses. Mulai dari atas ke bawah (1)

        for (i in 0 until length) {     // looping untuk setiap karakter dalam teks yang akan didekripsi
            fence[rail][i] = '*'                  // rail ke-0 & index ke-0 langsung dimasukkan ke dalam array [0][0]; rail ke-1 & index ke-1 langsung dimasukkan ke dalam array [1][1]; dst  Isinya adalah karakter `*` sebagai penanda
            rail += direction                     // mengatur pergerakan posisi karakter dalam pola zig-zag. (rail = rail + direction)

            // memeriksa apakah sudah mencapai batas paling bawah atau paling atas dari rail fence
            if (rail == rails - 1) {              // ketika rail sudah mencapai baris paling akhir, lalu menetapkan direction = -1 yang akan diproses lagi pada line `rail += direction`
                direction = -1
            } else if (rail == 0) {               // ketika rail sudah mencapai baris paling atas, lalu menetapkan direction = 1 yang akan diproses lagi pada line `rail += direction`
                direction = 1
            }
        }

        var index = 0
        // kedua for akan membuat looping secara horizontal untuk menggantikan karakter `*`
        for (r in 0 until rails) {            // for untuk menetapkan panjang baris
            for (i in 0 until length) {       // for untuk menetapkan panjang kolom
                if (fence[r][i] == '*' && index < cipherText.length) {  // pengecekan letak karakter `*` dalam kolom pada baris saat ini
                    fence[r][i] = cipherText[index]     // mengganti karakter `*` pada array 2 dimensi dengan isi karakter dari `cipherText` secara horizontal terlebih dahulu
                    index++
                }
            }
        }

        rail = 0
        direction = 1
        val decryptedText = StringBuilder()

        for (i in 0 until length) {         // looping untuk setiap karakter dalam teks yang akan didekripsi
            decryptedText.append(fence[rail][i])      // menambahkan karakter `decryptedText` ke StringBuilder agar dapat menggunakan fungsi append()
            rail += direction                         // mengatur pergerakan posisi karakter dalam pola zig-zag. (rail = rail + direction)

            // memeriksa apakah sudah mencapai batas paling bawah atau paling atas dari rail fence
            if (rail == rails - 1) {                  // ketika rail sudah mencapai baris paling akhir, lalu menetapkan direction = -1 yang akan diproses lagi pada line `rail += direction`
                direction = -1
            } else if (rail == 0) {                   // ketika rail sudah mencapai baris paling atas, lalu menetapkan direction = 1 yang akan diproses lagi pada line `rail += direction`
                direction = 1
            }
        }

        return decryptedText.toString()               // mengembalikan teks yang telah didekripsi sebagai string
    }
}