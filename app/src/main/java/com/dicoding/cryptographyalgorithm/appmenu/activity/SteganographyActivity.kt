package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.LSBSteganography
import com.dicoding.cryptographyalgorithm.appmenu.getImageUri
import com.dicoding.cryptographyalgorithm.databinding.ActivitySteganographyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SteganographyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySteganographyBinding
    private var currentImageUri: Uri? = null
    private var secretImageUri: Uri? = null
    private var embeddedBitmap: Bitmap? = null
    private var decodedBitmap: Bitmap? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            showToast(if (isGranted) "Permission request granted" else "Permission request denied")
        }

    private lateinit var saveImageLauncher: ActivityResultLauncher<Intent>

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySteganographyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
        initializeSaveImageLauncher()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.chooseSecretButton.setOnClickListener { startSecretImageSelection() }
        // Proses embedding gambar
        binding.embedButton.setOnClickListener { embedImage() }
        // Proses decoding gambar
        binding.decodeButton.setOnClickListener { decodeImage() }

        binding.saveEmbeddedButton.setOnClickListener { saveEmbeddedImage() }
        binding.saveDecodedButton.setOnClickListener { saveDecodedImage() }
    }

    private fun showProgress(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    private fun updateEmbedDecodeStatus(status: String) {
        binding.statusEmbedDecode.text = "Status: $status"
    }

    private fun updateSavingStatus(status: String) {
        binding.statusSaving.text = "Status: $status"
    }

    private fun checkPermissions() {
        if (!allPermissionsGranted()) requestPermissionLauncher.launch(REQUIRED_PERMISSION)
    }

    private fun initializeSaveImageLauncher() {
        saveImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    Log.d("Steganography", "Saving image to URI: $uri")
                    lifecycleScope.launch(Dispatchers.IO) { // Gunakan Dispatchers.IO untuk operasi I/O
                        try {
                            val bitmapToSave = if (uri.toString().contains("embedded_image")) embeddedBitmap else decodedBitmap
                            if (bitmapToSave == null) {
                                withContext(Dispatchers.Main) {
                                    showToast("Bitmap is null, nothing to save")
                                    updateSavingStatus("Saving failed")
                                }
                                Log.e("Steganography", "Bitmap is null")
                                return@launch
                            }

                            contentResolver.openOutputStream(uri)?.use { outputStream ->
                                val isSuccess = bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                                withContext(Dispatchers.Main) {
                                    if (isSuccess) {
                                        showToast("Image saved successfully")
                                        Log.d("Steganography", "Image saved successfully")
                                        updateSavingStatus("Saving finished")
                                    } else {
                                        showToast("Failed to compress image")
                                        Log.e("Steganography", "Failed to compress image")
                                        updateSavingStatus("Saving failed")
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                showToast("Error saving image: ${e.message}")
                                updateSavingStatus("Saving failed")
                            }
                            Log.e("Steganography", "Error saving image: ${e.message}")
                        }
                    }
                } ?: run {
                    showToast("No URI returned from save intent")
                    Log.e("Steganography", "No URI returned from save intent")
                    updateSavingStatus("Saving failed")
                }
            } else {
                showToast("Save operation cancelled")
                Log.d("Steganography", "Save operation cancelled")
                updateSavingStatus("Saving cancelled")
            }
        }
    }

    // Fungsi untuk memilih gambar dari galeri
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        currentImageUri = uri
        if (uri != null) showImage() else Log.d("Photo Picker", "No media selected")
    }

    // Fungsi untuk memilih gambar dari kamera
    private fun startCamera() {
        currentImageUri = getImageUri(this)
        currentImageUri?.let { launcherIntentCamera.launch(it) }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) showImage()
    }

    // Fungsi untuk memilih gambar rahasia
    private fun startSecretImageSelection() {
        launcherSecretImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherSecretImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        secretImageUri = uri
        binding.secretImageView.setImageURI(uri ?: return@registerForActivityResult)
    }

    private fun embedImage(){
        if (currentImageUri == null || secretImageUri == null) {
            showToast("Please select both cover and secret images")
            return
        }

        showProgress(true)
        updateEmbedDecodeStatus("Embedding started")

        lifecycleScope.launch {
            val coverBitmap = withContext(Dispatchers.IO) { loadBitmapFromUri(currentImageUri!!) }
            val secretBitmap = withContext(Dispatchers.IO) { loadBitmapFromUri(secretImageUri!!) }

            if (coverBitmap != null && secretBitmap != null) {
                val embeddedImage = withContext(Dispatchers.Default) {
                    LSBSteganography.embedImage(coverBitmap, secretBitmap)
                }
                embeddedBitmap = embeddedImage
                binding.resultImageView.setImageBitmap(embeddedImage)

                updateEmbedDecodeStatus("Embedding finished")
            } else {
                showToast("Failed to load images")
                updateEmbedDecodeStatus("Embedding failed")
            }

            showProgress(false)
        }
    }

    private fun decodeImage() {
        currentImageUri?.let { uri ->
            showProgress(true)
            updateEmbedDecodeStatus("Decoding started")

            lifecycleScope.launch {
                val bitmap = withContext(Dispatchers.IO) { loadBitmapFromUri(uri) }
                bitmap?.let {
                    val decodedImage = withContext(Dispatchers.Default) { LSBSteganography.decodeImage(it) }
                    decodedBitmap = decodedImage
                    binding.resultImageView.setImageBitmap(decodedImage)

                    updateEmbedDecodeStatus("Decoding finished")
                } ?: run {
                    showToast("Failed to load cover image")
                    updateEmbedDecodeStatus("Decoding failed")
                }

                showProgress(false)
            }
        } ?: showToast("Please select a cover image first")
    }

    private fun saveEmbeddedImage(){
        updateSavingStatus("Saving embedded image started")
        Log.d("Steganography", "Save Embedded Button Clicked")
        if (embeddedBitmap != null) {
            saveImage(embeddedBitmap!!, "embedded_image.png")
        } else {
            showToast("No embedded image to save")
            updateSavingStatus("Saving embedded image failed")
        }
    }

    private fun saveDecodedImage(){
        updateSavingStatus("Saving decoded image started")
        Log.d("Steganography", "Save Decoded Button Clicked")
        if (decodedBitmap != null) {
            saveImage(decodedBitmap!!, "decoded_image.png")
        } else {
            showToast("No decoded image to save")
            updateSavingStatus("Saving decoded image failed")
        }
    }

    // Fungsi untuk menampilkan gambar yang dipilih
    private fun showImage() {
        currentImageUri?.let {
            if (it.toString().isNotEmpty()) {
                binding.previewImageView.setImageURI(it)
            } else {
                showToast("Invalid image URI")
            }
        }
    }

    // Fungsi untuk memuat bitmap dari URI
    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                }
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
            // Salin bitmap ke konfigurasi ARGB_8888 agar bisa dimodifikasi
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        } catch (e: Exception) {
            Log.e("Steganography", "Error loading bitmap: ${e.message}")
            showToast("Failed to load image")
            null
        }
    }

    // Fungsi untuk menyimpan gambar
    private fun saveImage(bitmap: Bitmap, defaultFileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, defaultFileName)
        }
        saveImageLauncher.launch(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}