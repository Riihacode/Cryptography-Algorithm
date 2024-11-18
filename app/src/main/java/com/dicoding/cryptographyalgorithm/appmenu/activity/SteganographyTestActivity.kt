package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.cryptographyalgorithm.appmenu.SteganographyUtil
import com.dicoding.cryptographyalgorithm.databinding.ActivitySteganographyTestBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
/*
class SteganographyTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySteganographyTestBinding
    private var coverImage: Bitmap? = null
    private var encodedImage: Bitmap? = null
    private var decodedImage: Bitmap? = null
    private var secretImage: Bitmap? = null

    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var saveImageLauncher: ActivityResultLauncher<Intent>
    private var currentRequestCode: Int = 0
    private var isSavingDecodedImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySteganographyTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActivityResultLaunchers()

        binding.btnSelectCoverImage.setOnClickListener { selectImage(100) }
        binding.btnSelectSecretImage.setOnClickListener { selectImage(200) }
        binding.btnEncode.setOnClickListener { encodeImage() }
        binding.btnDecode.setOnClickListener { decodeImage() }
        binding.btnSaveEncodedImage.setOnClickListener { saveEncodedImage() }
        binding.btnSaveDecodedImage.setOnClickListener { saveDecodedImage() }
    }

    private fun initActivityResultLaunchers() {
        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val bitmap = decodeBitmapFromUri(uri)

                if (bitmap != null) {
                    val hasSignature = SteganographyUtil.hasSignature(bitmap)
                    Log.d("Steganography", "Has signature: $hasSignature")

                    if (currentRequestCode == 100) {
                        if (hasSignature) {
                            encodedImage = bitmap
                            coverImage = null
                            Toast.makeText(this, "Encoded image selected", Toast.LENGTH_SHORT).show()
                        } else {
                            coverImage = bitmap
                            encodedImage = null
                            Toast.makeText(this, "Cover image selected", Toast.LENGTH_SHORT).show()
                        }
                    } else if (currentRequestCode == 200) {
                        // Simpan secret image tanpa resize
                        secretImage = bitmap
                        Toast.makeText(this, "Secret image selected", Toast.LENGTH_SHORT).show()
                        Log.d("Steganography", "Secret image selected, size: ${secretImage?.width}x${secretImage?.height}")
                    }

                } else {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        saveImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data?.data ?: return@registerForActivityResult
                if (isSavingDecodedImage) {
                    saveBitmapToUri(decodedImage!!, uri)
                    isSavingDecodedImage = false
                } else {
                    saveBitmapToUri(encodedImage!!, uri)
                }
            }
        }
    }

    private fun selectImage(requestCode: Int) {
        currentRequestCode = requestCode
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        selectImageLauncher.launch(intent)
    }

    private fun encodeImage() {
        if (coverImage == null || secretImage == null) {
            Toast.makeText(this, "Please select both cover and secret images", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            encodedImage = withContext(Dispatchers.IO) {
                SteganographyUtil.encode(coverImage!!, secretImage!!)
            }
            binding.progressBar.visibility = View.GONE

            if (encodedImage != null) {
                binding.imageViewResult.setImageBitmap(encodedImage)
                Toast.makeText(this@SteganographyTestActivity, "Image encoded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun decodeImage() {
        val imageToDecode = encodedImage ?: coverImage

        if (imageToDecode == null) {
            Toast.makeText(this, "Please select an image to decode", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            decodedImage = withContext(Dispatchers.IO) {
                SteganographyUtil.decode(imageToDecode!!)
            }
            binding.progressBar.visibility = View.GONE

            if (decodedImage == null) {
                Toast.makeText(this@SteganographyTestActivity, "Invalid encoded image", Toast.LENGTH_SHORT).show()
            } else {
                binding.imageViewResult.setImageBitmap(decodedImage)
                Toast.makeText(this@SteganographyTestActivity, "Image decoded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveEncodedImage() {
        if (encodedImage == null) {
            Toast.makeText(this, "No encoded image to save", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, "encoded_image.png")
        }
        saveImageLauncher.launch(intent)
    }

    private fun saveDecodedImage() {
        if (decodedImage == null) {
            Toast.makeText(this, "No decoded image to save", Toast.LENGTH_SHORT).show()
            return
        }

        isSavingDecodedImage = true
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, "decoded_image.png")
        }
        saveImageLauncher.launch(intent)
    }

    private fun saveBitmapToUri(bitmap: Bitmap, uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SteganographyTestActivity, "Image saved successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SteganographyTestActivity, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun decodeBitmapFromUri(uri: Uri): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = false
            inSampleSize = 1
        }
        return contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }
}

 */

 */
/*
class SteganographyTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySteganographyTestBinding
    private var coverImage: Bitmap? = null
    private var encodedImage: Bitmap? = null
    private var decodedImage: Bitmap? = null
    private var secretImage: Bitmap? = null

    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var saveImageLauncher: ActivityResultLauncher<Intent>
    private var currentRequestCode: Int = 0
    private var isSavingDecodedImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySteganographyTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActivityResultLaunchers()

        binding.btnSelectCoverImage.setOnClickListener { selectImage(100) }
        binding.btnSelectSecretImage.setOnClickListener { selectImage(200) }
        binding.btnEncode.setOnClickListener { encodeImage() }
        binding.btnDecode.setOnClickListener { decodeImage() }
        binding.btnSaveEncodedImage.setOnClickListener { saveEncodedImage() }
        binding.btnSaveDecodedImage.setOnClickListener { saveDecodedImage() }
    }

    private fun initActivityResultLaunchers() {
        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val bitmap = decodeBitmapFromUri(uri)

                if (bitmap != null) {
                    val hasSignature = SteganographyUtil.hasSignature(bitmap)
                    Log.d("Steganography", "Has signature: $hasSignature")

                    if (currentRequestCode == 100) {
                        if (hasSignature) {
                            encodedImage = bitmap
                            coverImage = null
                            Toast.makeText(this, "Encoded image selected", Toast.LENGTH_SHORT).show()
                        } else {
                            coverImage = bitmap
                            encodedImage = null
                            Toast.makeText(this, "Cover image selected", Toast.LENGTH_SHORT).show()
                        }
                    } else if (currentRequestCode == 200) {
                        if (coverImage != null) {
                            // Debugging: Log ukuran coverImage dan secretImage
                            Log.d("Steganography", "Cover image size: ${coverImage!!.width}x${coverImage!!.height}")
                            Log.d("Steganography", "Secret image size: ${bitmap.width}x${bitmap.height}")

                            // Resize secret image jika cover image sudah dipilih
                            if (bitmap.width > coverImage!!.width || bitmap.height > coverImage!!.height) {
                                Toast.makeText(this, "Secret image cannot be larger than cover image", Toast.LENGTH_SHORT).show()
                                return@registerForActivityResult
                            }
                            secretImage = bitmap
                        } else {
                            // Simpan secret image tanpa resize jika cover image belum dipilih
                            secretImage = bitmap
                        }
                        Toast.makeText(this, "Secret image selected", Toast.LENGTH_SHORT).show()
                        Log.d("Steganography", "Secret image selected, size: ${secretImage?.width}x${secretImage?.height}")
                    }

                } else {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        saveImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data?.data ?: return@registerForActivityResult
                if (isSavingDecodedImage) {
                    saveBitmapToUri(decodedImage!!, uri)
                    isSavingDecodedImage = false
                } else {
                    saveBitmapToUri(encodedImage!!, uri)
                }
            }
        }
    }

    private fun selectImage(requestCode: Int) {
        currentRequestCode = requestCode
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        selectImageLauncher.launch(intent)
    }

    private fun encodeImage() {
        if (coverImage == null || secretImage == null) {
            Toast.makeText(this, "Please select both cover and secret images", Toast.LENGTH_SHORT).show()
            return
        }

        // Debugging: Log ukuran coverImage dan secretImage sebelum encoding
        Log.d("Steganography", "Encoding: Cover image size: ${coverImage!!.width}x${coverImage!!.height}")
        Log.d("Steganography", "Encoding: Secret image size: ${secretImage!!.width}x${secretImage!!.height}")

        // Validasi bahwa secret image tidak lebih besar dari cover image
        if (secretImage!!.width > coverImage!!.width || secretImage!!.height > coverImage!!.height) {
            Toast.makeText(this, "Secret image cannot be larger than cover image", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            encodedImage = withContext(Dispatchers.IO) {
                SteganographyUtil.encode(coverImage!!, secretImage!!)
            }
            binding.progressBar.visibility = View.GONE

            if (encodedImage != null) {
                binding.imageViewResult.setImageBitmap(encodedImage)
                Toast.makeText(this@SteganographyTestActivity, "Image encoded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun decodeImage() {
        val imageToDecode = encodedImage ?: coverImage

        if (imageToDecode == null) {
            Toast.makeText(this, "Please select an image to decode", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            decodedImage = withContext(Dispatchers.IO) {
                SteganographyUtil.decode(imageToDecode!!)
            }
            binding.progressBar.visibility = View.GONE

            if (decodedImage == null) {
                Toast.makeText(this@SteganographyTestActivity, "Invalid encoded image", Toast.LENGTH_SHORT).show()
            } else {
                binding.imageViewResult.setImageBitmap(decodedImage)
                Toast.makeText(this@SteganographyTestActivity, "Image decoded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveEncodedImage() {
        if (encodedImage == null) {
            Toast.makeText(this, "No encoded image to save", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, "encoded_image.png")
        }
        saveImageLauncher.launch(intent)
    }

    private fun saveDecodedImage() {
        if (decodedImage == null) {
            Toast.makeText(this, "No decoded image to save", Toast.LENGTH_SHORT).show()
            return
        }

        isSavingDecodedImage = true
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, "decoded_image.png")
        }
        saveImageLauncher.launch(intent)
    }

    private fun saveBitmapToUri(bitmap: Bitmap, uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SteganographyTestActivity, "Image saved successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SteganographyTestActivity, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun decodeBitmapFromUri(uri: Uri): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = false
            inSampleSize = 1
        }
        return contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }
}


 */

 */
class SteganographyTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySteganographyTestBinding
    private var coverImage: Bitmap? = null
    private var secretImage: Bitmap? = null
    private var encodedImage: Bitmap? = null
    private var decodedImage: Bitmap? = null

    private lateinit var selectImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var saveImageLauncher: ActivityResultLauncher<Intent>
    private var currentRequestCode: Int = 0
    private var isSavingDecodedImage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySteganographyTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActivityResultLaunchers()

        binding.btnSelectCoverImage.setOnClickListener { selectImage(100) }
        binding.btnSelectSecretImage.setOnClickListener { selectImage(200) }
        binding.btnEncode.setOnClickListener { encodeImage() }
        binding.btnDecode.setOnClickListener { decodeImage() }
        binding.btnSaveEncodedImage.setOnClickListener { saveEncodedImage() }
        binding.btnSaveDecodedImage.setOnClickListener { saveDecodedImage() }
    }

    private fun initActivityResultLaunchers() {
        selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val bitmap = decodeBitmapFromUri(uri)

                when (currentRequestCode) {
                    100 -> {
                        coverImage = bitmap
                        Toast.makeText(this, "Cover image selected", Toast.LENGTH_SHORT).show()
                    }
                    200 -> {
                        secretImage = bitmap
                        // Jangan resize secretImage, biarkan dimensi aslinya
                        Toast.makeText(this, "Secret image selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        saveImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data?.data ?: return@registerForActivityResult
                if (isSavingDecodedImage) {
                    saveBitmapToUri(decodedImage!!, uri)
                    isSavingDecodedImage = false
                } else {
                    saveBitmapToUri(encodedImage!!, uri)
                }
            }
        }
    }

    private fun selectImage(requestCode: Int) {
        currentRequestCode = requestCode
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        selectImageLauncher.launch(intent)
    }

    private fun encodeImage() {
        if (coverImage == null || secretImage == null) {
            Toast.makeText(this, "Please select both cover and secret images", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            encodedImage = withContext(Dispatchers.IO) {
                SteganographyUtil.encode(coverImage!!, secretImage!!)
            }
            binding.progressBar.visibility = View.GONE

            if (encodedImage != null) {
                binding.imageViewResult.setImageBitmap(encodedImage)
                Toast.makeText(this@SteganographyTestActivity, "Image encoded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun decodeImage() {
        if (coverImage == null) {
            Toast.makeText(this, "Please select an encoded image", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            decodedImage = withContext(Dispatchers.IO) {
                SteganographyUtil.decode(coverImage!!, secretImage!!) // Kirim coverImage dan secretImage yang asli
            }
            binding.progressBar.visibility = View.GONE

            if (decodedImage != null) {
                binding.imageViewResult.setImageBitmap(decodedImage)
                Toast.makeText(this@SteganographyTestActivity, "Image decoded successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveEncodedImage() {
        if (encodedImage == null) {
            Toast.makeText(this, "No encoded image to save", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, "encoded_image.png")
        }
        saveImageLauncher.launch(intent)
    }

    private fun saveDecodedImage() {
        if (decodedImage == null) {
            Toast.makeText(this, "No decoded image to save", Toast.LENGTH_SHORT).show()
            return
        }

        isSavingDecodedImage = true
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, "decoded_image.png")
        }
        saveImageLauncher.launch(intent)
    }

    private fun saveBitmapToUri(bitmap: Bitmap, uri: Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SteganographyTestActivity, "Image saved successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SteganographyTestActivity, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun decodeBitmapFromUri(uri: Uri): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = false
            inSampleSize = 1
        }
        return contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }
}
