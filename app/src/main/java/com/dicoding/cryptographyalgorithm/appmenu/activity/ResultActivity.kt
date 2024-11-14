package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)

        Log.d("ResultActivity", "Received Image URI: $imageUri")  // Debug log
        if (imageUri != null) {
            try {
                val contentUri = Uri.parse(imageUri)
                val inputStream = contentResolver.openInputStream(contentUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.resultImage.setImageBitmap(bitmap)
                inputStream?.close()
            } catch (e: Exception) {
                Log.e("ResultActivity", "Error loading bitmap: ${e.message}")  // Debug log for exceptions
                e.printStackTrace()  // Debug log for exception stack trace
            }
        } else {
            Log.d("ResultActivity", "No image URI provided")  // Debug log if URI is null
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_RESULT = "extra_result"
    }
}