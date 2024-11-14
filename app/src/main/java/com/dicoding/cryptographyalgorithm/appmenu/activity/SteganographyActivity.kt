package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.dicoding.cryptographyalgorithm.appmenu.getImageUri
import com.dicoding.cryptographyalgorithm.databinding.ActivitySteganographyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class SteganographyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySteganographyBinding
    private var currentImageUri: Uri? = null
    private var secretImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySteganographyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        // Pilih gambar utama dari gallery
        binding.galleryButton.setOnClickListener { startGallery() }

        // Pilih gambar utama dari kamera
        binding.cameraButton.setOnClickListener { startCamera() }

        // Pilih gambar utama menggunakan CameraX
        binding.cameraXButton.setOnClickListener { startCameraX() }

        // Pilih gambar rahasia
        binding.chooseSecretButton.setOnClickListener { startSecretImageSelection() }

        // Proses embedding gambar
        binding.embedButton.setOnClickListener {
            currentImageUri?.let { coverUri ->
                secretImageUri?.let { secretUri ->
                    lifecycleScope.launch {
                        try {
                            // Mendapatkan bitmap dari cover dan secret image
                            val coverBitmap = withContext(Dispatchers.IO) {
                                MediaStore.Images.Media.getBitmap(contentResolver, coverUri)
                            }

                            val secretBitmap = withContext(Dispatchers.IO) {
                                MediaStore.Images.Media.getBitmap(contentResolver, secretUri)
                            }

                            // Embed gambar secara asynchronous
                            val embeddedImage = withContext(Dispatchers.Default) {
                                embedImage(coverBitmap, secretBitmap)
                            }

                            // Simpan hasil embedding ke penyimpanan perangkat
                            val embeddedImageUri = saveBitmapToStorage(embeddedImage)

                            // Tampilkan hasil embedding pada resultImageView
                            withContext(Dispatchers.Main) {
                                binding.resultImageView.setImageBitmap(embeddedImage)
                            }

                            // Jika berhasil, log URI tempat penyimpanan
                            embeddedImageUri?.let {
                                Log.d("Steganography", "Embedded Image saved at: $embeddedImageUri")
                            } ?: Log.e("Steganography", "Failed to save embedded image")

                        } catch (e: Exception) {
                            Log.e("Steganography", "Error during embedding: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        binding.decodeButton.setOnClickListener {
            currentImageUri?.let { imageUri ->
                lifecycleScope.launch {
                    try {
                        val bitmap = withContext(Dispatchers.IO) {
                            MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                        }
                        val decodedImage = withContext(Dispatchers.Default) {
                            decodeImage(bitmap)
                        }

                        // Tampilkan hasil decode pada decodedImageView
                        withContext(Dispatchers.Main) {
                            binding.resultImageView.setImageBitmap(decodedImage)
                        }
                    } catch (e: Exception) {
                        Log.e("Steganography", "Error decoding image: ${e.message}")
                    }
                }
            }
        }
    }

    // Fungsi untuk mendecode gambar yang telah disisipkan
    private fun decodeImage(embeddedBitmap: Bitmap): Bitmap {
        val result = Bitmap.createBitmap(embeddedBitmap.width, embeddedBitmap.height, Bitmap.Config.ARGB_8888)

        for (x in 0 until embeddedBitmap.width) {
            for (y in 0 until embeddedBitmap.height) {
                val pixel = embeddedBitmap.getPixel(x, y)

                // Mengambil bit terakhir dari setiap channel warna untuk mendapatkan data rahasia
                val red = (Color.red(pixel) and 0x01) shl 7
                val green = (Color.green(pixel) and 0x01) shl 7
                val blue = (Color.blue(pixel) and 0x01) shl 7

                // Menyusun kembali pixel berdasarkan bit-bit tersebut
                val decodedPixel = Color.rgb(red, green, blue)
                result.setPixel(x, y, decodedPixel)
            }
        }
        return result
    }

    private fun embedImage(cover: Bitmap, secret: Bitmap): Bitmap {
        val result = cover.copy(cover.config, true)

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

    // Fungsi untuk memilih gambar dari galeri
    private fun startGallery() {
        Log.d("Steganography", "Opening gallery to pick cover image")  // Debug log
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    // Fungsi untuk memilih gambar dari kamera
    private fun startCamera() {
        currentImageUri = getImageUri(this)
        currentImageUri?.let { uri ->
            Log.d("Steganography", "Launching camera with URI: $uri")  // Debug log
            launcherIntentCamera.launch(uri)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    // Fungsi untuk memilih gambar dengan CameraX
    private fun startCameraX() {
        Log.d("Steganography", "Starting CameraX for cover image")  // Debug log
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    // Fungsi untuk menampilkan gambar yang dipilih
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    // Fungsi untuk memilih gambar rahasia
    private fun startSecretImageSelection() {
        launcherSecretImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherSecretImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            secretImageUri = uri
            binding.secretImageView.setImageURI(uri)
        } else {
            Log.d("Secret Image Picker", "No secret image selected")
        }
    }

    // Fungsi untuk menyimpan gambar hasil embedding ke penyimpanan
    @SuppressLint("Recycle")
    private fun saveBitmapToStorage(bitmap: Bitmap): Uri? {
        val filename = "embedded_image_${System.currentTimeMillis()}.png"
        var embeddedImageUri: Uri? = null

        try {
            val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Steganography")
                    }
                )?.also { uri ->
                    embeddedImageUri = uri
                }?.let { contentResolver.openOutputStream(it) }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(imagesDir, filename)
                embeddedImageUri = Uri.fromFile(file)
                FileOutputStream(file)
            }

            fos?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }

            // Pindai file agar terdeteksi oleh sistem
            MediaScannerConnection.scanFile(
                this,
                arrayOf(embeddedImageUri?.path),
                null
            ) { path, uri ->
                Log.d("Steganography", "MediaScanner finished scanning file: $path")
            }

            return embeddedImageUri
        } catch (e: IOException) {
            Log.e("Steganography", "Error saving bitmap: ${e.message}")
        }

        return embeddedImageUri
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
