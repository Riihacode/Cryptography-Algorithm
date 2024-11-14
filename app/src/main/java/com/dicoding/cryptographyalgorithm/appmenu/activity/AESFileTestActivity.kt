package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicoding.cryptographyalgorithm.R
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.AESFileTest
import com.dicoding.cryptographyalgorithm.databinding.ActivityAesfileTestBinding
import java.io.File
import javax.crypto.SecretKey

class AESFileTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAesfileTestBinding
    private var selectedFileUri: Uri? = null
    private var encryptionUri: Uri? = null
    private var decryptionUri: Uri? = null
    private var isEncryptedFile = false
    private var tempDecryptedData: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAesfileTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectFile.setOnClickListener { selectFile() }
        binding.btnEncrypt.setOnClickListener {
            if (isEncryptedFile) {
                Toast.makeText(this, "Cannot encrypt an already encrypted file (.bin).", Toast.LENGTH_SHORT).show()
            } else {
                selectSaveLocationForEncryption()
            }
        }
        binding.btnDecrypt.setOnClickListener {
            if (!isEncryptedFile) {
                Toast.makeText(this, "Cannot decrypt a non-encrypted file (image).", Toast.LENGTH_SHORT).show()
            } else {
                decryptFile()
            }
        }
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, 100)
    }

    private fun selectSaveLocationForEncryption() {
        val secretKey = getKeyFromInput()
        if (secretKey == null) {
            Toast.makeText(this, "Please enter a valid key (16, 24, or 32 characters)", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "encrypted_image.bin")
        }
        startActivityForResult(intent, 200)
    }

    private fun selectSaveLocationForDecryption() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, "decrypted_image.png")
        }
        startActivityForResult(intent, 300)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            when (requestCode) {
                100 -> {
                    selectedFileUri = uri
                    val fileName = uri?.lastPathSegment ?: "Unknown file"
                    binding.tvSelectedImage.text = "Selected file: $fileName"

                    val extension = getFileExtension(uri)
                    isEncryptedFile = extension == "bin"

                    if (isEncryptedFile) {
                        Toast.makeText(this, "Encrypted file selected (.bin). Ready for decryption.", Toast.LENGTH_SHORT).show()
                    } else if (extension in listOf("png", "jpg", "jpeg")) {
                        Toast.makeText(this, "Image file selected ($extension). Ready for encryption.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Invalid file type selected.", Toast.LENGTH_SHORT).show()
                        selectedFileUri = null
                    }
                }
                200 -> {
                    encryptionUri = uri
                    encryptFile()
                }
                300 -> {
                    decryptionUri = uri
                    if (tempDecryptedData != null && tempDecryptedData!!.isNotEmpty()) {
                        saveDecryptedFile(tempDecryptedData!!, decryptionUri!!)
                        tempDecryptedData = null
                    } else {
                        Toast.makeText(this, "Decryption failed: Invalid key.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getKeyFromInput(): SecretKey? {
        val keyString = binding.etKey.text.toString()

        // Validasi panjang kunci (16, 24, atau 32 karakter)
        if (keyString.length != 16 && keyString.length != 24 && keyString.length != 32) {
            Toast.makeText(this, "Key length must be 16, 24, or 32 characters long.", Toast.LENGTH_SHORT).show()
            return null
        }

        return try {
            AESFileTest.generateAESKeyFromString(keyString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun getFileExtension(uri: Uri?): String {
        return uri?.let {
            val mimeType = contentResolver.getType(it)
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: ""
        } ?: ""
    }

    private fun encryptFile() {
        val secretKey = getKeyFromInput() ?: return
        if (selectedFileUri == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            return
        }

        val inputData = contentResolver.openInputStream(selectedFileUri!!)?.readBytes() ?: return
        val encryptedData = AESFileTest.encryptData(inputData, secretKey)
        contentResolver.openOutputStream(encryptionUri!!)?.use { it.write(encryptedData) }
        Toast.makeText(this, "Encryption successful", Toast.LENGTH_SHORT).show()
    }

    private fun decryptFile() {
        val secretKey = getKeyFromInput() ?: return
        if (selectedFileUri == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val encryptedData = contentResolver.openInputStream(selectedFileUri!!)?.readBytes() ?: return
            val decryptedData = AESFileTest.decryptData(encryptedData, secretKey)

            if (decryptedData.isEmpty()) {
                Toast.makeText(this, "Decryption failed: Invalid key.", Toast.LENGTH_SHORT).show()
            } else {
                tempDecryptedData = decryptedData
                selectSaveLocationForDecryption()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Decryption error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDecryptedFile(decryptedData: ByteArray, uri: Uri) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(decryptedData)
                Toast.makeText(this, "Decryption successful", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

