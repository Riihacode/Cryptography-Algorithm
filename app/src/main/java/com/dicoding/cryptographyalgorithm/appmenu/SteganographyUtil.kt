package com.dicoding.cryptographyalgorithm.appmenu

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log

// 1 LSB
/*
object SteganographyUtil {

    // Fungsi encode menggunakan LSB murni (1-bit)
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height

        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x < secretImage.width && y < secretImage.height) {
                    val coverPixel = coverImage.getPixel(x, y)
                    val secretPixel = secretImage.getPixel(x, y)

                    // Mengambil channel warna dari secretPixel (1-bit paling signifikan)
                    val secretRedBit = (secretPixel shr 16) and 0x80 shr 7
                    val secretGreenBit = (secretPixel shr 8) and 0x80 shr 7
                    val secretBlueBit = secretPixel and 0x80 shr 7

                    // Mengambil channel warna dari coverPixel
                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    // Menyisipkan 1-bit paling rendah dari secret image ke LSB cover image
                    val newRed = (coverRed and 0xFE) or secretRedBit
                    val newGreen = (coverGreen and 0xFE) or secretGreenBit
                    val newBlue = (coverBlue and 0xFE) or secretBlueBit

                    // Membuat pixel baru
                    val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                    encodedImage.setPixel(x, y, newPixel)
                }
            }
        }

        return encodedImage
    }

    // Fungsi decode menggunakan LSB murni (1-bit)
    fun decode(encodedImage: Bitmap): Bitmap {
        val width = encodedImage.width
        val height = encodedImage.height

        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val encodedPixel = encodedImage.getPixel(x, y)

                // Mengambil LSB dari setiap channel warna
                val redBit = (encodedPixel shr 16) and 0x01
                val greenBit = (encodedPixel shr 8) and 0x01
                val blueBit = encodedPixel and 0x01

                // Membentuk kembali channel warna dengan bit yang diambil
                val decodedRed = redBit shl 7
                val decodedGreen = greenBit shl 7
                val decodedBlue = blueBit shl 7

                // Membuat pixel baru untuk decoded image
                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */


// 2 LSB
/*
object SteganographyUtil {
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height

        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x < secretImage.width && y < secretImage.height) {
                    val coverPixel = coverImage.getPixel(x, y)
                    val secretPixel = secretImage.getPixel(x, y)

                    // Mengambil channel warna dari secretPixel
                    val secretRed = (secretPixel shr 16) and 0xFF
                    val secretGreen = (secretPixel shr 8) and 0xFF
                    val secretBlue = secretPixel and 0xFF

                    // Mengambil 2 bit paling signifikan dari secret image
                    val secretRedBits = secretRed shr 6
                    val secretGreenBits = secretGreen shr 6
                    val secretBlueBits = secretBlue shr 6

                    // Mengambil channel warna dari coverPixel
                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    // Menyisipkan 2 bit dari secret image ke dalam cover image
                    val newRed = (coverRed and 0xFC) or secretRedBits
                    val newGreen = (coverGreen and 0xFC) or secretGreenBits
                    val newBlue = (coverBlue and 0xFC) or secretBlueBits

                    // Membuat pixel baru
                    val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                    encodedImage.setPixel(x, y, newPixel)
                }
            }
        }

        return encodedImage
    }

    fun decode(encodedImage: Bitmap): Bitmap {
        val width = encodedImage.width
        val height = encodedImage.height

        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val encodedPixel = encodedImage.getPixel(x, y)

                // Mengambil 2 bit paling tidak signifikan dari setiap channel
                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = encodedPixel and 0x03

                // Menggeser bit untuk membentuk kembali channel warna
                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                // Membuat pixel baru untuk decoded image
                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */




// 2 LSB + Penanda Signature untuk validasi
/*
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature di piksel terakhir
    fun hasSignature(image: Bitmap): Boolean {
        val width = image.width
        val height = image.height
        val lastPixel = image.getPixel(width - 1, height - 1)
        val redBits = (lastPixel shr 16) and 0x03
        Log.d("Steganography", "Last pixel red bits: $redBits")
        return redBits == SIGNATURE
    }

    // Fungsi encode menggunakan LSB 2-bit dengan bit penanda di piksel terakhir
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height
        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        // Menyisipkan bit penanda di piksel terakhir
        val lastPixel = coverImage.getPixel(width - 1, height - 1)
        val red = (lastPixel shr 16) and 0xFF
        val newRed = (red and 0xFC) or SIGNATURE
        val newLastPixel = (0xFF shl 24) or (newRed shl 16) or (lastPixel and 0x00FFFF)
        encodedImage.setPixel(width - 1, height - 1, newLastPixel)

        // Memulai proses encoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir
                if (x < secretImage.width && y < secretImage.height) {
                    val coverPixel = coverImage.getPixel(x, y)
                    val secretPixel = secretImage.getPixel(x, y)

                    val secretRedBits = (secretPixel shr 16) and 0xFF shr 6
                    val secretGreenBits = (secretPixel shr 8) and 0xFF shr 6
                    val secretBlueBits = secretPixel and 0xFF shr 6

                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    val newRed = (coverRed and 0xFC) or secretRedBits
                    val newGreen = (coverGreen and 0xFC) or secretGreenBits
                    val newBlue = (coverBlue and 0xFC) or secretBlueBits

                    val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                    encodedImage.setPixel(x, y, newPixel)
                }
            }
        }

        return encodedImage
    }

    // Fungsi decode dengan validasi bit penanda di piksel terakhir
    fun decode(encodedImage: Bitmap): Bitmap? {
        if (!hasSignature(encodedImage)) {
            Log.d("Steganography", "Signature not found in the last pixel")
            return null
        }

        val width = encodedImage.width
        val height = encodedImage.height
        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir
                val encodedPixel = encodedImage.getPixel(x, y)

                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = encodedPixel and 0x03

                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */
/*
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature di piksel terakhir
    fun hasSignature(image: Bitmap): Boolean {
        val width = image.width
        val height = image.height
        val lastPixel = image.getPixel(width - 1, height - 1)
        val redBits = (lastPixel shr 16) and 0x03
        Log.d("Steganography", "Last pixel red bits: $redBits")
        return redBits == SIGNATURE
    }

    // Fungsi encode menggunakan LSB 2-bit dengan bit penanda di piksel terakhir
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height
        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        // Menyisipkan bit penanda di piksel terakhir
        val lastPixel = coverImage.getPixel(width - 1, height - 1)
        val red = (lastPixel shr 16) and 0xFF
        val newRed = (red and 0xFC) or SIGNATURE
        val newLastPixel = (0xFF shl 24) or (newRed shl 16) or (lastPixel and 0x00FFFF)
        encodedImage.setPixel(width - 1, height - 1, newLastPixel)

        // Memulai proses encoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir

                // Mengambil piksel dari secret image dengan wrapping jika lebih kecil
                val secretX = x % secretImage.width
                val secretY = y % secretImage.height
                val secretPixel = secretImage.getPixel(secretX, secretY)

                val coverPixel = coverImage.getPixel(x, y)

                // Mengambil 2-bit paling signifikan dari secret image
                val secretRedBits = (secretPixel shr 16) and 0xFF shr 6
                val secretGreenBits = (secretPixel shr 8) and 0xFF shr 6
                val secretBlueBits = secretPixel and 0xFF shr 6

                // Mengambil channel warna dari coverPixel
                val coverRed = (coverPixel shr 16) and 0xFF
                val coverGreen = (coverPixel shr 8) and 0xFF
                val coverBlue = coverPixel and 0xFF

                // Menyisipkan 2-bit dari secret image ke dalam 2-bit LSB cover image
                val newRed = (coverRed and 0xFC) or secretRedBits
                val newGreen = (coverGreen and 0xFC) or secretGreenBits
                val newBlue = (coverBlue and 0xFC) or secretBlueBits

                // Membuat piksel baru dengan channel yang telah dimodifikasi
                val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                encodedImage.setPixel(x, y, newPixel)
            }
        }

        return encodedImage
    }

    // Fungsi decode untuk mengekstraksi secret image dari encoded image
    fun decode(encodedImage: Bitmap): Bitmap? {
        if (!hasSignature(encodedImage)) {
            Log.d("Steganography", "Signature not found in the last pixel")
            return null
        }

        val width = encodedImage.width
        val height = encodedImage.height
        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Memulai proses decoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir
                val encodedPixel = encodedImage.getPixel(x, y)

                // Mengambil 2-bit paling tidak signifikan dari setiap channel warna
                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = encodedPixel and 0x03

                // Menggeser bit untuk membentuk kembali channel warna secret image
                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                // Membuat piksel baru untuk decoded image
                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */
/*
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature di piksel terakhir
    fun hasSignature(image: Bitmap?): Boolean {
        if (image == null) return false
        return try {
            val width = image.width
            val height = image.height
            val lastPixel = image.getPixel(width - 1, height - 1)
            val redBits = (lastPixel shr 16) and 0x03
            Log.d("Steganography", "Last pixel red bits: $redBits")
            redBits == SIGNATURE
        } catch (e: Exception) {
            Log.e("SteganographyUtil", "Error checking signature: ${e.message}")
            false
        }
    }

    // Fungsi encode dengan error handling tambahan
    fun encode(coverImage: Bitmap?, secretImage: Bitmap?): Bitmap? {
        if (coverImage == null || secretImage == null) {
            Log.e("SteganographyUtil", "Cover image or secret image is null")
            return null
        }

        return try {
            val width = coverImage.width
            val height = coverImage.height
            val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

            // Menyisipkan bit penanda di piksel terakhir
            val lastPixel = coverImage.getPixel(width - 1, height - 1)
            val red = (lastPixel shr 16) and 0xFF
            val newRed = (red and 0xFC) or SIGNATURE
            val newLastPixel = (0xFF shl 24) or (newRed shl 16) or (lastPixel and 0x00FFFF)
            encodedImage.setPixel(width - 1, height - 1, newLastPixel)

            // Memulai proses encoding
            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (x == width - 1 && y == height - 1) continue

                    val secretX = x % secretImage.width
                    val secretY = y % secretImage.height

                    val secretPixel = secretImage.getPixel(secretX, secretY)
                    val coverPixel = coverImage.getPixel(x, y)

                    // Mengambil 2-bit paling signifikan dari secret image
                    val secretRedBits = (secretPixel shr 16) and 0xFF shr 6
                    val secretGreenBits = (secretPixel shr 8) and 0xFF shr 6
                    val secretBlueBits = secretPixel and 0xFF shr 6

                    // Mengambil channel warna dari coverPixel
                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    // Menyisipkan 2-bit ke dalam 2-bit LSB
                    val newRed = (coverRed and 0xFC) or secretRedBits
                    val newGreen = (coverGreen and 0xFC) or secretGreenBits
                    val newBlue = (coverBlue and 0xFC) or secretBlueBits

                    val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                    encodedImage.setPixel(x, y, newPixel)
                }
            }

            encodedImage
        } catch (e: OutOfMemoryError) {
            Log.e("SteganographyUtil", "Out of memory during encoding: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("SteganographyUtil", "Error during encoding: ${e.message}")
            null
        }
    }

    // Fungsi decode dengan error handling tambahan
    fun decode(encodedImage: Bitmap?): Bitmap? {
        if (encodedImage == null) {
            Log.e("SteganographyUtil", "Encoded image is null")
            return null
        }

        return try {
            if (!hasSignature(encodedImage)) {
                Log.d("SteganographyUtil", "Signature not found in the last pixel")
                return null
            }

            val width = encodedImage.width
            val height = encodedImage.height
            val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (x == width - 1 && y == height - 1) continue
                    val encodedPixel = encodedImage.getPixel(x, y)

                    val redBits = (encodedPixel shr 16) and 0x03
                    val greenBits = (encodedPixel shr 8) and 0x03
                    val blueBits = encodedPixel and 0x03

                    val decodedRed = redBits shl 6
                    val decodedGreen = greenBits shl 6
                    val decodedBlue = blueBits shl 6

                    val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                    decodedImage.setPixel(x, y, decodedPixel)
                }
            }

            decodedImage
        } catch (e: OutOfMemoryError) {
            Log.e("SteganographyUtil", "Out of memory during decoding: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e("SteganographyUtil", "Error during decoding: ${e.message}")
            null
        }
    }
}

 */

/*
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature
    fun hasSignature(image: Bitmap): Boolean {
        val firstPixel = image.getPixel(0, 0)
        val firstRed = (firstPixel shr 16) and 0x03
        return firstRed == SIGNATURE
    }

    // Fungsi encode menggunakan LSB 2-bit dengan bit penanda
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height
        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        // Menyisipkan bit penanda pada piksel pertama (x = 0, y = 0)
        val firstPixel = coverImage.getPixel(0, 0)
        val firstRed = (firstPixel shr 16) and 0xFF
        val newFirstRed = (firstRed and 0xFC) or SIGNATURE
        val newFirstPixel = (0xFF shl 24) or (newFirstRed shl 16) or (firstPixel and 0x00FFFF)
        encodedImage.setPixel(0, 0, newFirstPixel)

        // Memulai proses encoding pada piksel berikutnya
        for (x in 0 until width) {
            val startY = if (x == 0) 1 else 0  // Menghindari piksel pertama
            for (y in startY until height) {
                if (x < secretImage.width && y < secretImage.height) {
                    val coverPixel = coverImage.getPixel(x, y)
                    val secretPixel = secretImage.getPixel(x, y)

                    // Mengambil 2-bit paling signifikan dari secret image
                    val secretRedBits = (secretPixel shr 16) and 0xFF shr 6
                    val secretGreenBits = (secretPixel shr 8) and 0xFF shr 6
                    val secretBlueBits = secretPixel and 0xFF shr 6

                    // Mengambil channel warna dari coverPixel
                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    // Menyisipkan 2-bit dari secret image ke dalam 2-bit LSB cover image
                    val newRed = (coverRed and 0xFC) or secretRedBits
                    val newGreen = (coverGreen and 0xFC) or secretGreenBits
                    val newBlue = (coverBlue and 0xFC) or secretBlueBits

                    // Membuat piksel baru dengan channel yang telah dimodifikasi
                    val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                    encodedImage.setPixel(x, y, newPixel)
                }
            }
        }

        return encodedImage
    }

    // Fungsi decode untuk mengekstraksi secret image dari encoded image
    fun decode(encodedImage: Bitmap): Bitmap? {
        val width = encodedImage.width
        val height = encodedImage.height

        // Memeriksa bit penanda pada piksel pertama
        if (!hasSignature(encodedImage)) {
            return null  // Gambar bukan hasil encoding
        }

        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Memulai proses decoding pada piksel berikutnya
        for (x in 0 until width) {
            val startY = if (x == 0) 1 else 0  // Menghindari piksel pertama
            for (y in startY until height) {
                val encodedPixel = encodedImage.getPixel(x, y)

                // Mengambil 2-bit paling tidak signifikan dari setiap channel warna
                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = encodedPixel and 0x03

                // Menggeser bit untuk membentuk kembali channel warna secret image
                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                // Membuat piksel baru untuk decoded image
                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */

/*
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature di piksel terakhir
    fun hasSignature(image: Bitmap): Boolean {
        val width = image.width
        val height = image.height
        val lastPixel = image.getPixel(width - 1, height - 1)
        val redBits = (lastPixel shr 16) and 0x03
        Log.d("Steganography", "Last pixel red bits: $redBits")
        return redBits == SIGNATURE
    }

    // Fungsi encode menggunakan LSB 2-bit dengan bit penanda di piksel terakhir
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height
        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        // Menyisipkan bit penanda di piksel terakhir
        val lastPixel = coverImage.getPixel(width - 1, height - 1)
        val red = (lastPixel shr 16) and 0xFF
        val newRed = (red and 0xFC) or SIGNATURE
        val newLastPixel = (0xFF shl 24) or (newRed shl 16) or (lastPixel and 0x00FFFF)
        encodedImage.setPixel(width - 1, height - 1, newLastPixel)

        // Memulai proses encoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir
                if (x < secretImage.width && y < secretImage.height) {
                    val coverPixel = coverImage.getPixel(x, y)
                    val secretPixel = secretImage.getPixel(x, y)

                    val secretRedBits = (secretPixel shr 16) and 0xFF shr 6
                    val secretGreenBits = (secretPixel shr 8) and 0xFF shr 6
                    val secretBlueBits = secretPixel and 0xFF shr 6

                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    val newRed = (coverRed and 0xFC) or secretRedBits
                    val newGreen = (coverGreen and 0xFC) or secretGreenBits
                    val newBlue = (coverBlue and 0xFC) or secretBlueBits

                    val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                    encodedImage.setPixel(x, y, newPixel)
                }
            }
        }

        return encodedImage
    }

    // Fungsi decode dengan validasi bit penanda di piksel terakhir
    fun decode(encodedImage: Bitmap): Bitmap? {
        if (!hasSignature(encodedImage)) {
            Log.d("Steganography", "Signature not found in the last pixel")
            return null
        }

        val width = encodedImage.width
        val height = encodedImage.height
        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir
                val encodedPixel = encodedImage.getPixel(x, y)

                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = encodedPixel and 0x03

                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */
/*
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature di piksel terakhir
    fun hasSignature(image: Bitmap): Boolean {
        val width = image.width
        val height = image.height
        val lastPixel = image.getPixel(width - 1, height - 1)
        val redBits = (lastPixel shr 16) and 0x03
        Log.d("Steganography", "Last pixel red bits: $redBits")
        return redBits == SIGNATURE
    }

    // Fungsi encode menggunakan LSB 2-bit dengan bit penanda di piksel terakhir
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height
        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        // Menyisipkan bit penanda di piksel terakhir
        val lastPixel = coverImage.getPixel(width - 1, height - 1)
        val red = (lastPixel shr 16) and 0xFF
        val newRed = (red and 0xFC) or SIGNATURE
        val newLastPixel = (0xFF shl 24) or (newRed shl 16) or (lastPixel and 0x00FFFF)
        encodedImage.setPixel(width - 1, height - 1, newLastPixel)

        // Memulai proses encoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir

                // Mengambil piksel dari secret image dengan wrapping jika lebih kecil
                val secretX = x % secretImage.width
                val secretY = y % secretImage.height
                val secretPixel = secretImage.getPixel(secretX, secretY)

                val coverPixel = coverImage.getPixel(x, y)

                // Mengambil 2-bit paling signifikan dari secret image
                val secretRedBits = (secretPixel shr 16) and 0xFF shr 6
                val secretGreenBits = (secretPixel shr 8) and 0xFF shr 6
                val secretBlueBits = secretPixel and 0xFF shr 6

                // Mengambil channel warna dari coverPixel
                val coverRed = (coverPixel shr 16) and 0xFF
                val coverGreen = (coverPixel shr 8) and 0xFF
                val coverBlue = coverPixel and 0xFF

                // Menyisipkan 2-bit dari secret image ke dalam 2-bit LSB cover image
                val newRed = (coverRed and 0xFC) or secretRedBits
                val newGreen = (coverGreen and 0xFC) or secretGreenBits
                val newBlue = (coverBlue and 0xFC) or secretBlueBits

                // Membuat piksel baru dengan channel yang telah dimodifikasi
                val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                encodedImage.setPixel(x, y, newPixel)
            }
        }

        return encodedImage
    }

    // Fungsi decode untuk mengekstraksi secret image dari encoded image
    fun decode(encodedImage: Bitmap): Bitmap? {
        if (!hasSignature(encodedImage)) {
            Log.d("Steganography", "Signature not found in the last pixel")
            return null
        }

        val width = encodedImage.width
        val height = encodedImage.height
        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Memulai proses decoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir
                val encodedPixel = encodedImage.getPixel(x, y)

                // Mengambil 2-bit paling tidak signifikan dari setiap channel warna
                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = encodedPixel and 0x03

                // Menggeser bit untuk membentuk kembali channel warna secret image
                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                // Membuat piksel baru untuk decoded image
                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */

// So far the best
/*
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature di piksel terakhir
    fun hasSignature(image: Bitmap): Boolean {
        val width = image.width
        val height = image.height
        val lastPixel = image.getPixel(width - 1, height - 1)
        val redBits = (lastPixel shr 16) and 0x03
        Log.d("Steganography", "Last pixel red bits: $redBits")
        return redBits == SIGNATURE
    }

    // Fungsi encode menggunakan LSB 2-bit dengan bit penanda di piksel terakhir
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height
        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        // Menyisipkan bit penanda di piksel terakhir
        val lastPixel = coverImage.getPixel(width - 1, height - 1)
        val red = (lastPixel shr 16) and 0xFF
        val newRed = (red and 0xFC) or SIGNATURE
        val newLastPixel = (0xFF shl 24) or (newRed shl 16) or (lastPixel and 0x00FFFF)
        encodedImage.setPixel(width - 1, height - 1, newLastPixel)

        // Memulai proses encoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir

                // Mengambil piksel dari secret image dengan wrapping jika lebih kecil
                val secretX = x % secretImage.width
                val secretY = y % secretImage.height
                val secretPixel = secretImage.getPixel(secretX, secretY)

                val coverPixel = coverImage.getPixel(x, y)

                // Mengambil 2-bit paling signifikan dari secret image
                val secretRedBits = (secretPixel shr 16) and 0xFF shr 6
                val secretGreenBits = (secretPixel shr 8) and 0xFF shr 6
                val secretBlueBits = secretPixel and 0xFF shr 6

                // Mengambil channel warna dari coverPixel
                val coverRed = (coverPixel shr 16) and 0xFF
                val coverGreen = (coverPixel shr 8) and 0xFF
                val coverBlue = coverPixel and 0xFF

                // Menyisipkan 2-bit dari secret image ke dalam 2-bit LSB cover image
                val newRed = (coverRed and 0xFC) or secretRedBits
                val newGreen = (coverGreen and 0xFC) or secretGreenBits
                val newBlue = (coverBlue and 0xFC) or secretBlueBits

                // Membuat piksel baru dengan channel yang telah dimodifikasi
                val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                encodedImage.setPixel(x, y, newPixel)
            }
        }

        return encodedImage
    }

    // Fungsi decode untuk mengekstraksi secret image dari encoded image
    fun decode(encodedImage: Bitmap): Bitmap? {
        if (!hasSignature(encodedImage)) {
            Log.d("Steganography", "Signature not found in the last pixel")
            return null
        }

        val width = encodedImage.width
        val height = encodedImage.height
        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Memulai proses decoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir
                val encodedPixel = encodedImage.getPixel(x, y)

                // Mengambil 2-bit paling tidak signifikan dari setiap channel warna
                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = encodedPixel and 0x03

                // Menggeser bit untuk membentuk kembali channel warna secret image
                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                // Membuat piksel baru untuk decoded image
                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */

/*
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature di piksel terakhir
    fun hasSignature(image: Bitmap): Boolean {
        val width = image.width
        val height = image.height
        val lastPixel = image.getPixel(width - 1, height - 1)
        val redBits = (lastPixel shr 16) and 0x03
        Log.d("Steganography", "Last pixel red bits: $redBits")
        return redBits == SIGNATURE
    }

    // Fungsi encode menggunakan LSB 2-bit dengan bit penanda di piksel terakhir
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height
        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        // Memastikan hanya satu secret image yang disisipkan
        val secretWidth = secretImage.width
        val secretHeight = secretImage.height

        // Menyisipkan bit penanda di piksel terakhir
        val lastPixel = coverImage.getPixel(width - 1, height - 1)
        val red = (lastPixel shr 16) and 0xFF
        val newRed = (red and 0xFC) or SIGNATURE
        val newLastPixel = (0xFF shl 24) or (newRed shl 16) or (lastPixel and 0x00FFFF)
        encodedImage.setPixel(width - 1, height - 1, newLastPixel)

        // Sisipkan secret image ke dalam cover image
        var xOffset = 0
        var yOffset = 0
        for (x in 0 until secretWidth) {
            for (y in 0 until secretHeight) {
                if (x < width && y < height) {
                    val coverPixel = coverImage.getPixel(x, y)
                    val secretPixel = secretImage.getPixel(x, y)

                    val secretRedBits = (secretPixel shr 16) and 0xFF shr 6
                    val secretGreenBits = (secretPixel shr 8) and 0xFF shr 6
                    val secretBlueBits = secretPixel and 0xFF shr 6

                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    val newRed = (coverRed and 0xFC) or secretRedBits
                    val newGreen = (coverGreen and 0xFC) or secretGreenBits
                    val newBlue = (coverBlue and 0xFC) or secretBlueBits

                    val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                    encodedImage.setPixel(x, y, newPixel)
                }
            }
        }

        return encodedImage
    }

    // Fungsi decode dengan validasi bit penanda di piksel terakhir
    fun decode(encodedImage: Bitmap): Bitmap? {
        if (!hasSignature(encodedImage)) {
            Log.d("Steganography", "Signature not found in the last pixel")
            return null
        }

        val width = encodedImage.width
        val height = encodedImage.height
        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir
                val encodedPixel = encodedImage.getPixel(x, y)

                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = (encodedPixel and 0x03)

                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */
/*
object SteganographyUtil {
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height

        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x < secretImage.width && y < secretImage.height) {
                    val coverPixel = coverImage.getPixel(x, y)
                    val secretPixel = secretImage.getPixel(x, y)

                    // Mengambil channel warna dari secretPixel
                    val secretRed = (secretPixel shr 16) and 0xFF
                    val secretGreen = (secretPixel shr 8) and 0xFF
                    val secretBlue = secretPixel and 0xFF

                    // Mengambil 2 bit paling signifikan dari secret image
                    val secretRedBits = secretRed shr 6
                    val secretGreenBits = secretGreen shr 6
                    val secretBlueBits = secretBlue shr 6

                    // Mengambil channel warna dari coverPixel
                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    // Menyisipkan 2 bit dari secret image ke dalam cover image
                    val newRed = (coverRed and 0xFC) or secretRedBits
                    val newGreen = (coverGreen and 0xFC) or secretGreenBits
                    val newBlue = (coverBlue and 0xFC) or secretBlueBits

                    // Membuat pixel baru
                    val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                    encodedImage.setPixel(x, y, newPixel)
                }
            }
        }

        return encodedImage
    }

    fun decode(encodedImage: Bitmap): Bitmap {
        val width = encodedImage.width
        val height = encodedImage.height

        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val encodedPixel = encodedImage.getPixel(x, y)

                // Mengambil 2 bit paling tidak signifikan dari setiap channel
                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = encodedPixel and 0x03

                // Menggeser bit untuk membentuk kembali channel warna
                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                // Membuat pixel baru untuk decoded image
                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */
object SteganographyUtil {

    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height

        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x < secretImage.width && y < secretImage.height) {
                    val coverPixel = coverImage.getPixel(x, y)
                    val secretPixel = secretImage.getPixel(x, y)

                    // Mengambil channel warna dari secretPixel
                    val secretRed = (secretPixel shr 16) and 0xFF
                    val secretGreen = (secretPixel shr 8) and 0xFF
                    val secretBlue = secretPixel and 0xFF

                    // Mengambil 2 bit paling signifikan dari secret image
                    val secretRedBits = secretRed shr 6
                    val secretGreenBits = secretGreen shr 6
                    val secretBlueBits = secretBlue shr 6

                    // Mengambil channel warna dari coverPixel
                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    // Menyisipkan 2 bit dari secret image ke dalam cover image
                    val newRed = (coverRed and 0xFC) or secretRedBits
                    val newGreen = (coverGreen and 0xFC) or secretGreenBits
                    val newBlue = (coverBlue and 0xFC) or secretBlueBits

                    // Membuat pixel baru
                    val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                    encodedImage.setPixel(x, y, newPixel)
                }
            }
        }

        return encodedImage
    }

    fun decode(encodedImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = secretImage.width // Gunakan dimensi asli dari secret image
        val height = secretImage.height

        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val encodedPixel = encodedImage.getPixel(x, y)

                // Mengambil 2 bit paling tidak signifikan dari setiap channel
                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = encodedPixel and 0x03

                // Menggeser bit untuk membentuk kembali channel warna
                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                // Membuat pixel baru untuk decoded image
                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}


/*
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature di piksel terakhir
    fun hasSignature(image: Bitmap): Boolean {
        val width = image.width
        val height = image.height
        val lastPixel = image.getPixel(width - 1, height - 1)
        val redBits = (lastPixel shr 16) and 0x03
        Log.d("Steganography", "Last pixel red bits: $redBits")
        return redBits == SIGNATURE
    }

    // Fungsi encode menggunakan LSB 2-bit dengan bit penanda di piksel terakhir
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height
        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        // Resize secret image jika perlu, untuk memastikan ukuran yang sesuai dengan cover image
        val resizedSecretImage = resizeSecretImage(secretImage, width, height)

        // Menyisipkan bit signature di piksel terakhir
        val lastPixel = coverImage.getPixel(width - 1, height - 1)
        val red = (lastPixel shr 16) and 0xFF
        val newRed = (red and 0xFC) or SIGNATURE // 0xFC = 11111100, SIGNATURE = 11 (2-bit)
        val newLastPixel = (0xFF shl 24) or (newRed shl 16) or (lastPixel and 0x00FFFF)
        encodedImage.setPixel(width - 1, height - 1, newLastPixel)

        // Proses encoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip last pixel

                // Mengambil piksel dari resized secret image dengan wrapping menggunakan modulo
                val secretX = x % resizedSecretImage.width
                val secretY = y % resizedSecretImage.height
                val secretPixel = resizedSecretImage.getPixel(secretX, secretY)

                val coverPixel = coverImage.getPixel(x, y)

                // Mengambil 2-bit dari secret image
                val secretRedBits = (secretPixel shr 16) and 0xFF shr 6
                val secretGreenBits = (secretPixel shr 8) and 0xFF shr 6
                val secretBlueBits = secretPixel and 0xFF shr 6

                // Menyisipkan 2-bit dari secret image ke dalam 2-bit LSB cover image
                val coverRed = (coverPixel shr 16) and 0xFF
                val coverGreen = (coverPixel shr 8) and 0xFF
                val coverBlue = coverPixel and 0xFF

                val newRed = (coverRed and 0xFC) or secretRedBits
                val newGreen = (coverGreen and 0xFC) or secretGreenBits
                val newBlue = (coverBlue and 0xFC) or secretBlueBits

                val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                encodedImage.setPixel(x, y, newPixel)
            }
        }

        return encodedImage
    }

    // Fungsi untuk resize secret image sesuai dengan ukuran cover image
    fun resizeSecretImage(secretImage: Bitmap, coverWidth: Int, coverHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(secretImage, coverWidth, coverHeight, false)
    }


    // Fungsi decode untuk mengekstraksi secret image dari encoded image
    fun decode(encodedImage: Bitmap): Bitmap? {
        if (!hasSignature(encodedImage)) {
            Log.d("Steganography", "Signature not found in the last pixel")
            return null
        }

        val width = encodedImage.width
        val height = encodedImage.height
        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Proses decoding untuk piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip last pixel

                val encodedPixel = encodedImage.getPixel(x, y)

                // Mengambil 2-bit LSB dari setiap channel warna
                val redBits = (encodedPixel shr 16) and 0x03  // Ambil 2 bit LSB dari red
                val greenBits = (encodedPixel shr 8) and 0x03  // Ambil 2 bit LSB dari green
                val blueBits = (encodedPixel) and 0x03  // Ambil 2 bit LSB dari blue

                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }

}
*/

/*
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature di piksel terakhir
    fun hasSignature(image: Bitmap): Boolean {
        val width = image.width
        val height = image.height
        val lastPixel = image.getPixel(width - 1, height - 1)
        val redBits = (lastPixel shr 16) and 0x03
        Log.d("Steganography", "Last pixel red bits: $redBits")
        return redBits == SIGNATURE
    }

    // Fungsi encode menggunakan LSB 2-bit dengan bit penanda di piksel terakhir
    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height
        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        // Menyisipkan bit penanda di piksel terakhir
        val lastPixel = coverImage.getPixel(width - 1, height - 1)
        val red = (lastPixel shr 16) and 0xFF
        val newRed = (red and 0xFC) or SIGNATURE
        val newLastPixel = (0xFF shl 24) or (newRed shl 16) or (lastPixel and 0x00FFFF)
        encodedImage.setPixel(width - 1, height - 1, newLastPixel)

        // Hitung perbandingan rasio antara cover image dan secret image
        val secretWidth = secretImage.width
        val secretHeight = secretImage.height
        val widthRatio = width.toFloat() / secretWidth.toFloat()
        val heightRatio = height.toFloat() / secretHeight.toFloat()

        // Memulai proses encoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir

                // Menghitung posisi pada secret image berdasarkan rasio
                val secretX = (x / widthRatio).toInt() % secretWidth
                val secretY = (y / heightRatio).toInt() % secretHeight

                val secretPixel = secretImage.getPixel(secretX, secretY)
                val coverPixel = coverImage.getPixel(x, y)

                // Mengambil 2-bit paling signifikan dari secret image
                val secretRedBits = (secretPixel shr 16) and 0xFF shr 6
                val secretGreenBits = (secretPixel shr 8) and 0xFF shr 6
                val secretBlueBits = secretPixel and 0xFF shr 6

                // Mengambil channel warna dari coverPixel
                val coverRed = (coverPixel shr 16) and 0xFF
                val coverGreen = (coverPixel shr 8) and 0xFF
                val coverBlue = coverPixel and 0xFF

                // Menyisipkan 2-bit dari secret image ke dalam 2-bit LSB cover image
                val newRed = (coverRed and 0xFC) or secretRedBits
                val newGreen = (coverGreen and 0xFC) or secretGreenBits
                val newBlue = (coverBlue and 0xFC) or secretBlueBits

                // Membuat piksel baru dengan channel yang telah dimodifikasi
                val newPixel = (0xFF shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
                encodedImage.setPixel(x, y, newPixel)
            }
        }

        return encodedImage
    }

    // Fungsi decode untuk mengekstraksi secret image dari encoded image
    fun decode(encodedImage: Bitmap): Bitmap? {
        if (!hasSignature(encodedImage)) {
            Log.d("Steganography", "Signature not found in the last pixel")
            return null
        }

        val width = encodedImage.width
        val height = encodedImage.height
        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Memulai proses decoding pada piksel lainnya
        for (x in 0 until width) {
            for (y in 0 until height) {
                if (x == width - 1 && y == height - 1) continue  // Skip piksel terakhir
                val encodedPixel = encodedImage.getPixel(x, y)

                // Mengambil 2-bit paling tidak signifikan dari setiap channel warna
                val redBits = (encodedPixel shr 16) and 0x03
                val greenBits = (encodedPixel shr 8) and 0x03
                val blueBits = encodedPixel and 0x03

                // Menggeser bit untuk membentuk kembali channel warna secret image
                val decodedRed = redBits shl 6
                val decodedGreen = greenBits shl 6
                val decodedBlue = blueBits shl 6

                // Membuat piksel baru untuk decoded image
                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */


