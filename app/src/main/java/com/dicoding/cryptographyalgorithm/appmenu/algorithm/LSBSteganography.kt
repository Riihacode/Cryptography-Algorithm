package com.dicoding.cryptographyalgorithm.appmenu.algorithm

import android.graphics.Bitmap
import android.graphics.Color

object LSBSteganography {
    fun embedImage(cover: Bitmap, secret: Bitmap): Bitmap {
        require(cover.width > 0 && cover.height > 0) { "Cover image dimensions are invalid" }
        require(secret.width > 0 && secret.height > 0) { "Secret image dimensions are invalid" }

        val result = cover.copy(Bitmap.Config.ARGB_8888, true)
        for (x in 0 until minOf(cover.width, secret.width)) {
            for (y in 0 until minOf(cover.height, secret.height)) {
                val coverPixel = cover.getPixel(x, y)
                val secretPixel = secret.getPixel(x, y)

                val newRed = (Color.red(coverPixel) and 0xFE) or (Color.red(secretPixel) shr 7)
                val newGreen = (Color.green(coverPixel) and 0xFE) or (Color.green(secretPixel) shr 7)
                val newBlue = (Color.blue(coverPixel) and 0xFE) or (Color.blue(secretPixel) shr 7)

                result.setPixel(x, y, Color.rgb(newRed, newGreen, newBlue))
            }
        }
        return result
    }

    fun decodeImage(embeddedBitmap: Bitmap): Bitmap {
        require(embeddedBitmap.width > 0 && embeddedBitmap.height > 0) { "Embedded image dimensions are invalid" }

        val result = Bitmap.createBitmap(embeddedBitmap.width, embeddedBitmap.height, Bitmap.Config.ARGB_8888)
        for (x in 0 until embeddedBitmap.width) {
            for (y in 0 until embeddedBitmap.height) {
                val pixel = embeddedBitmap.getPixel(x, y)

                val red = (Color.red(pixel) and 0x01) shl 7
                val green = (Color.green(pixel) and 0x01) shl 7
                val blue = (Color.blue(pixel) and 0x01) shl 7

                result.setPixel(x, y, Color.rgb(red, green, blue))
            }
        }
        return result
    }
}