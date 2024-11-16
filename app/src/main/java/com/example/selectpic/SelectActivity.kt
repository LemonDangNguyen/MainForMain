package com.example.selectpic

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.selectpic.databinding.ActivitySelectBinding

class SelectActivity : BaseActivity() {
    private val binding by lazy { ActivitySelectBinding.inflate(layoutInflater) }
    private val images = mutableListOf<ImageModel>()
    private val selectedImages = mutableListOf<ImageModel>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var selectedImagesAdapter: SelectedImagesAdapter

    private val storagePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    else arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                loadImages()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupRecyclerViews()

        if (hasStoragePermissions()) {
            loadImages()
        } else {
            permissionLauncher.launch(storagePermissions)
        }

        binding.clearImgList.setOnClickListener {
            selectedImages.clear()
            selectedImagesAdapter.notifyDataSetChanged()
            updateSelectedCount()
            imageAdapter.updateSelection(selectedImages)
        }
    }

    private fun setupRecyclerViews() {
        imageAdapter = ImageAdapter(this, images) { image, isSelected ->
            if (isSelected) {
                if (!selectedImages.contains(image) && selectedImages.size < 9) {
                    selectedImages.add(image)
                    updateSelectedAdapters()
                }
            } else {
                selectedImages.remove(image)
                updateSelectedAdapters()
            }
        }

        binding.allImagesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@SelectActivity, 3)
            adapter = imageAdapter
        }

        selectedImagesAdapter = SelectedImagesAdapter(this, selectedImages)
        binding.selectedImagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SelectActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = selectedImagesAdapter
        }

       binding.btnBack.setOnClickListener {
           onBackPressed()
       }
        binding.btnAlbum.setOnClickListener {
            val intent = Intent(this, SelectAlbum::class.java)
            startActivity(intent)
        }
    }
    private fun loadImages() {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )
        contentResolver.query(uri, projection, null, null, "${MediaStore.Images.Media.DATE_TAKEN} DESC")?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                images.add(
                    ImageModel(
                        id = cursor.getLong(idIndex),
                        dateTaken = cursor.getLong(dateIndex),
                        fileName = cursor.getString(nameIndex),
                        filePath = cursor.getString(pathIndex),
                        album = cursor.getString(albumIndex)
                    )
                )
            }
            imageAdapter.notifyDataSetChanged()
        }
    }
    private fun hasStoragePermissions() = storagePermissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
    private fun updateSelectedAdapters() {
        selectedImagesAdapter.notifyDataSetChanged()
        imageAdapter.updateSelection(selectedImages)
        updateSelectedCount()
    }
    private fun updateSelectedCount() {
        binding.textViewCountItem.text = selectedImages.size.toString()
    }
}

