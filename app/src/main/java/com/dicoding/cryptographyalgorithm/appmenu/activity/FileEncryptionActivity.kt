package com.dicoding.cryptographyalgorithm.appmenu.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.cryptographyalgorithm.appmenu.algorithm.AESFileEncryption
import com.dicoding.cryptographyalgorithm.databinding.ActivityFileEncryptionBinding
import javax.crypto.SecretKey

class FileEncryptionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFileEncryptionBinding
    private var selectedFileUri: Uri? = null
    private var encryptionUri: Uri? = null
    private var decryptionUri: Uri? = null
    private var isEncryptedFile = false
    private var tempDecryptedData: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileEncryptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSelectFile.setOnClickListener { selectFile() }

        binding.btnEncrypt.setOnClickListener {
            if (isEncryptedFile) {
                Toast.makeText(this, "Cannot encrypt an already encrypted file (.bin).", Toast.LENGTH_SHORT).show()
                updateStatus("Encryption failed: File is already encrypted.")
            } else {
                updateStatus("Encrypting...")
                selectSaveLocationForEncryption()
            }
        }

        binding.btnDecrypt.setOnClickListener {
            if (selectedFileUri == null) {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
                updateStatus("Decryption failed: No file selected.")
                return@setOnClickListener
            }

            if (!isEncryptedFile) {
                Toast.makeText(this, "Cannot decrypt a non-encrypted file (image).", Toast.LENGTH_SHORT).show()
                updateStatus("Decryption failed: File is not encrypted.")
                return@setOnClickListener
            }

            updateStatus("Decrypting...")
            decryptFile()
        }

    }

    private fun updateStatus(message: String) {
        binding.tvStatus.text = "Status: $message"
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
            updateStatus("Encryption failed: Invalid key.")
            return
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            updateStatus("Encryption failed: No file selected.")
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
        val secretKey = getKeyFromInput()
        if (secretKey == null) {
            Toast.makeText(this, "Please enter a valid key (16, 24, or 32 characters)", Toast.LENGTH_SHORT).show()
            updateStatus("Decryption failed: Invalid key.")
            return
        }

        if (selectedFileUri == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            updateStatus("Decryption failed: No file selected.")
            return
        }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(Intent.EXTRA_TITLE, "decrypted_image.png")
        }
        startActivityForResult(intent, 300)
    }

    @SuppressLint("SetTextI18n")
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            when (requestCode) {
                100 -> {
                    selectedFileUri = uri
                    val fileName = getFileName(uri)
                    binding.tvSelectedImage.text = "Selected file: $fileName"

                    val extension = getFileExtension(uri)
                    isEncryptedFile = extension == "bin"

                    if (isEncryptedFile) {
                        Toast.makeText(this, "Encrypted file selected (.bin). Ready for decryption.", Toast.LENGTH_SHORT).show()
                        updateStatus("Ready for decryption.")
                    } else if (extension in listOf("png", "jpg", "jpeg")) {
                        Toast.makeText(this, "Image file selected ($extension). Ready for encryption.", Toast.LENGTH_SHORT).show()
                        updateStatus("Ready for encryption.")
                    } else {
                        Toast.makeText(this, "Invalid file type selected.", Toast.LENGTH_SHORT).show()
                        updateStatus("Invalid file type selected.")
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
                        updateStatus("Decryption failed: Invalid key.")
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
            updateStatus("Invalid key length.")
            return null
        }

        return try {
            AESFileEncryption.generateAESKeyFromString(keyString)
        } catch (e: IllegalArgumentException) {
            updateStatus("Invalid key format.")
            null
        }
    }

    private fun getFileName(uri: Uri?): String {
        if (uri == null) return "Unknown file"

        var fileName = "Unknown file"
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        fileName = it.getString(nameIndex)
                    }
                }
            }
        } else if (uri.scheme == "file") {
            fileName = uri.lastPathSegment ?: "Unknown file"
        }

        return fileName
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
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            updateStatus("Encryption failed: No file selected.")
            return
        }

        try {
            val inputData = contentResolver.openInputStream(selectedFileUri!!)?.readBytes() ?: return
            val encryptedData = AESFileEncryption.encryptData(inputData, secretKey)
            contentResolver.openOutputStream(encryptionUri!!)?.use { it.write(encryptedData) }
            Toast.makeText(this, "Encryption successful", Toast.LENGTH_SHORT).show()
            updateStatus("Encryption successful.")
        } catch (e: Exception) {
            Toast.makeText(this, "Encryption error: ${e.message}", Toast.LENGTH_SHORT).show()
            updateStatus("Encryption failed: ${e.message}")
        }
    }

    private fun decryptFile() {
        val secretKey = getKeyFromInput() ?: return
        if (selectedFileUri == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
            updateStatus("Decryption failed: No file selected.")
            return
        }

        try {
            updateStatus("Decrypting...")
            val encryptedData = contentResolver.openInputStream(selectedFileUri!!)?.readBytes() ?: return
            val decryptedData = AESFileEncryption.decryptData(encryptedData, secretKey)

            if (decryptedData.isEmpty()) {
                Toast.makeText(this, "Decryption failed: Invalid key.", Toast.LENGTH_SHORT).show()
                updateStatus("Decryption failed: Invalid key.")
            } else {
                tempDecryptedData = decryptedData
                selectSaveLocationForDecryption()
                //updateStatus("Decryption successful.")
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Decryption error: ${e.message}", Toast.LENGTH_SHORT).show()
            updateStatus("Decryption failed: ${e.message}")
        }
    }

    private fun saveDecryptedFile(decryptedData: ByteArray, uri: Uri) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(decryptedData)
                Toast.makeText(this, "Decryption successful", Toast.LENGTH_SHORT).show()
                updateStatus("Decryption successful.")
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save file: ${e.message}", Toast.LENGTH_SHORT).show()
            updateStatus("Failed to save file: ${e.message}")
        }
    }
}