package com.dicoding.cryptographyalgorithm.appmenu

import android.graphics.Bitmap
import android.graphics.Color

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
object SteganographyUtil {
    private const val SIGNATURE = 0b11  // Bit penanda (2-bit: 11)

    // Fungsi untuk memeriksa apakah gambar memiliki signature
    fun hasSignature(image: Bitmap): Boolean {
        val firstPixel = image.getPixel(0, 0)
        val firstRed = (firstPixel shr 16) and 0x03
        return firstRed == SIGNATURE
    }

    fun encode(coverImage: Bitmap, secretImage: Bitmap): Bitmap {
        val width = coverImage.width
        val height = coverImage.height
        val encodedImage = coverImage.copy(Bitmap.Config.ARGB_8888, true)

        // Menyisipkan bit penanda pada piksel pertama
        val firstPixel = coverImage.getPixel(0, 0)
        val firstRed = (firstPixel shr 16) and 0xFF
        val newFirstRed = (firstRed and 0xFC) or SIGNATURE
        val newFirstPixel = (0xFF shl 24) or (newFirstRed shl 16) or (firstPixel and 0x00FFFF)
        encodedImage.setPixel(0, 0, newFirstPixel)

        // Proses encoding (sama seperti sebelumnya)
        for (x in 0 until width) {
            val startY = if (x == 0) 1 else 0
            for (y in startY until height) {
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

    fun decode(encodedImage: Bitmap): Bitmap? {
        val width = encodedImage.width
        val height = encodedImage.height

        // Validasi bit penanda pada piksel pertama
        if (!hasSignature(encodedImage)) {
            return null
        }

        val decodedImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            val startY = if (x == 0) 1 else 0
            for (y in startY until height) {
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




// 3 LSB
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

                    // Menyimpan 3 bit paling signifikan dari setiap channel secret ke dalam coverPixel
                    val newRed = (coverPixel shr 16 and 0xF8) or (secretRed shr 5)
                    val newGreen = (coverPixel shr 8 and 0xF8) or (secretGreen shr 5)
                    val newBlue = (coverPixel and 0xF8) or (secretBlue shr 5)

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

                // Mengambil 3 bit paling signifikan dari setiap channel
                val red = (encodedPixel shr 16 and 0x07) shl 5
                val green = (encodedPixel shr 8 and 0x07) shl 5
                val blue = (encodedPixel and 0x07) shl 5

                // Membuat pixel baru dengan warna dari secret image
                val decodedPixel = (0xFF shl 24) or (red shl 16) or (green shl 8) or blue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */
// Hybrid 2 & 3 LSB
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

                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    val secretRed = (secretPixel shr 16) and 0xFF
                    val secretGreen = (secretPixel shr 8) and 0xFF
                    val secretBlue = secretPixel and 0xFF

                    // Deteksi area detail atau warna solid
                    val isSolidColor = (coverRed > 240 && coverGreen > 240 && coverBlue > 240)

                    // Gunakan 2 LSB atau 3 LSB berdasarkan kondisi
                    val newRed = if (isSolidColor) {
                        (coverRed and 0xF8) or (secretRed shr 5)  // 3 LSB
                    } else {
                        (coverRed and 0xFC) or (secretRed shr 6)  // 2 LSB
                    }

                    val newGreen = if (isSolidColor) {
                        (coverGreen and 0xF8) or (secretGreen shr 5)  // 3 LSB
                    } else {
                        (coverGreen and 0xFC) or (secretGreen shr 6)  // 2 LSB
                    }

                    val newBlue = if (isSolidColor) {
                        (coverBlue and 0xF8) or (secretBlue shr 5)  // 3 LSB
                    } else {
                        (coverBlue and 0xFC) or (secretBlue shr 6)  // 2 LSB
                    }

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

                val red = (encodedPixel shr 16) and 0xFF
                val green = (encodedPixel shr 8) and 0xFF
                val blue = encodedPixel and 0xFF

                // Deteksi apakah menggunakan 2 LSB atau 3 LSB berdasarkan nilai warna
                val isSolidColor = (red > 240 && green > 240 && blue > 240)

                val decodedRed = if (isSolidColor) {
                    (red and 0x07) shl 5  // 3 LSB
                } else {
                    (red and 0x03) shl 6  // 2 LSB
                }

                val decodedGreen = if (isSolidColor) {
                    (green and 0x07) shl 5  // 3 LSB
                } else {
                    (green and 0x03) shl 6  // 2 LSB
                }

                val decodedBlue = if (isSolidColor) {
                    (blue and 0x07) shl 5  // 3 LSB
                } else {
                    (blue and 0x03) shl 6  // 2 LSB
                }

                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */

// 2 & 4 LSB
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

                    val coverRed = (coverPixel shr 16) and 0xFF
                    val coverGreen = (coverPixel shr 8) and 0xFF
                    val coverBlue = coverPixel and 0xFF

                    val secretRed = (secretPixel shr 16) and 0xFF
                    val secretGreen = (secretPixel shr 8) and 0xFF
                    val secretBlue = secretPixel and 0xFF

                    // Deteksi apakah pixel termasuk warna solid
                    val isSolidColor = (coverRed > 240 && coverGreen > 240 && coverBlue > 240) ||
                            (coverRed < 15 && coverGreen < 15 && coverBlue < 15)

                    // Menggunakan 4 LSB untuk warna solid, dan 2 LSB untuk area detail
                    val newRed = if (isSolidColor) {
                        (coverRed and 0xF0) or (secretRed shr 4)  // 4 LSB
                    } else {
                        (coverRed and 0xFC) or (secretRed shr 6)  // 2 LSB
                    }

                    val newGreen = if (isSolidColor) {
                        (coverGreen and 0xF0) or (secretGreen shr 4)  // 4 LSB
                    } else {
                        (coverGreen and 0xFC) or (secretGreen shr 6)  // 2 LSB
                    }

                    val newBlue = if (isSolidColor) {
                        (coverBlue and 0xF0) or (secretBlue shr 4)  // 4 LSB
                    } else {
                        (coverBlue and 0xFC) or (secretBlue shr 6)  // 2 LSB
                    }

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

                val red = (encodedPixel shr 16) and 0xFF
                val green = (encodedPixel shr 8) and 0xFF
                val blue = encodedPixel and 0xFF

                // Deteksi apakah menggunakan 2 LSB atau 4 LSB berdasarkan nilai warna
                val isSolidColor = (red > 240 && green > 240 && blue > 240) ||
                        (red < 15 && green < 15 && blue < 15)

                val decodedRed = if (isSolidColor) {
                    (red and 0x0F) shl 4  // 4 LSB
                } else {
                    (red and 0x03) shl 6  // 2 LSB
                }

                val decodedGreen = if (isSolidColor) {
                    (green and 0x0F) shl 4  // 4 LSB
                } else {
                    (green and 0x03) shl 6  // 2 LSB
                }

                val decodedBlue = if (isSolidColor) {
                    (blue and 0x0F) shl 4  // 4 LSB
                } else {
                    (blue and 0x03) shl 6  // 2 LSB
                }

                val decodedPixel = (0xFF shl 24) or (decodedRed shl 16) or (decodedGreen shl 8) or decodedBlue
                decodedImage.setPixel(x, y, decodedPixel)
            }
        }

        return decodedImage
    }
}

 */





