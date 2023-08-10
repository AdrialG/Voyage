package com.voyageteam.voyage.ui.profile

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.voyageteam.voyage.R
import com.voyageteam.voyage.base.BaseActivity
import com.voyageteam.voyage.data.Session
import com.voyageteam.voyage.databinding.ActivityProfileBinding
import com.voyageteam.voyage.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

    @Inject
    lateinit var session : Session

    private val galleryPermissionRequestCode = 100
    private var profilePictureUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        getUser()

        binding.profilePictureEdit.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                requestGalleryPermission()
            } else {
                openGallery()
            }
        }

        binding.editNameButton.setOnClickListener {
            val oldName = binding.profileName.textOf()
            showEditNameDialog(oldName)
        }

        binding.editPasswordButton.setOnClickListener {
            showEditPasswordDialog()
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            openActivity<LoginActivity>()
            finishAffinity()
        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.SUCCESS -> {
                                val user = session.getUser()
                                binding.data = user

                            }
                            ApiStatus.ERROR -> {
                                binding.root.snacked("Something Went Wrong")
                            }
                            else -> {

                            }
                        }
                    }
                }
            }
        }
    }

    private fun showEditNameDialog(oldName: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogue_edit_name, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        editTextName.setText(oldName)

        val builder = AlertDialog.Builder(this)
            .setTitle("Edit Name")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Save the edited name here
                val newName = editTextName.text.toString()
                // Do something with the new name (e.g., update UI, save to database, etc.)
                viewModel.updateProfileName(newName)

                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    // Set the new name to the old name TextView
                    binding.profileName.text = newName

                    binding.root.snacked("Name Updated")
                }

            }
            .setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()

    }

    private fun showEditPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialogue_edit_password, null)
        val editOldPassword = dialogView.findViewById<EditText>(R.id.editOldPassword)
        val editNewPassword = dialogView.findViewById<EditText>(R.id.editNewPassword)
        val editConfirmPassword = dialogView.findViewById<EditText>(R.id.editConfirmPassword)

        val builder = AlertDialog.Builder(this)
            .setTitle("Edit Password")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Save the edited name here
                val oldPass = editOldPassword.text.toString()
                val newPass = editNewPassword.text.toString()
                val confirmPass = editConfirmPassword.text.toString()
                // Do something with the new name (e.g., update UI, save to database, etc.)
                viewModel.updatePassword(oldPass, newPass, confirmPass)

                binding.root.snacked("Password Updated")
            }
            .setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun validateForm() {

        val name = binding.profileName.textOf()

        val compressedFilePicture = this.profilePictureUri?.let { uri ->
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val file = File.createTempFile("compressed_image", ".jpg", cacheDir)
                FileOutputStream(file).use { outputStream ->
                    compressImage(inputStream, outputStream)
                }
                file
            }
        }

        if (compressedFilePicture != null) {
            viewModel.updateProfile(name, compressedFilePicture)
        }
    }

    private fun getUser() {
        viewModel.getProfile()
    }

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        this.profilePictureUri = uri
        this.profilePictureUri?.let { selectedUri ->
            // Call compressImage from a coroutine
            lifecycleScope.launch {
                val compressedImage = compressImage(selectedUri)
                // Set the compressed image to the ImageView
                binding.profilePicture.setImageURI(compressedImage)

                validateForm()

            }
        }
    }

    private fun openGallery() {
        pickImage.launch("image/*")
    }

    private fun isGalleryPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestGalleryPermission() {
        if (isGalleryPermissionGranted()) {
            // Permission already granted, perform gallery-related operations
            openGallery()

        } else {
            // For Android 10 and above, request the permission directly
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                galleryPermissionRequestCode
            )
        }
    }

    private suspend fun compressImage(uri: Uri): Uri? = withContext(Dispatchers.IO) {
        val inputStream = contentResolver.openInputStream(uri)
        val outputFile = File.createTempFile("compressed_image", ".jpg")
        val outputStream = FileOutputStream(outputFile)
        compressImage(inputStream, outputStream)
        inputStream?.close()
        outputStream.close()
        return@withContext Uri.fromFile(outputFile)
    }

    private fun compressImage(inputStream: InputStream?, outputStream: OutputStream) {
        inputStream?.let { input ->
            val options = BitmapFactory.Options().apply {
                this.inSampleSize = 2 // Adjust the inSampleSize as needed for your requirements
            }
            val bitmap = BitmapFactory.decodeStream(input, null, options)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            bitmap?.recycle()
        }
    }

}