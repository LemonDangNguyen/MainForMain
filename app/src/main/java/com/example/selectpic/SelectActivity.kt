package com.example.selectpic

import android.Manifest
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

class SelectActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySelectBinding.inflate(layoutInflater) }
    private val imagePaths = mutableListOf<String>()
    private val selectedImagesList = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var selectedImagesAdapter: SelectedImagesAdapter
    private lateinit var pickImagesLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    val storagePer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    else arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    private val checkPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {

                loadImages()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }


        val gridLayoutManager = GridLayoutManager(this, 3)
        binding.allImagesRecyclerView.layoutManager = gridLayoutManager

        pickImagesLauncher = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
            if (uris.isNotEmpty()) {
                selectedImagesList.clear()
                selectedImagesList.addAll(uris)
                selectedImagesAdapter.notifyDataSetChanged()
                updateSelectedCount()
            }
        }
        if (checkStoragePermission()) {
            loadImages()

        } else {
            checkPermission.launch(storagePer)
        }
        setupSelectedImagesRecyclerView()
        binding.btnAlbum.setOnClickListener {
            pickImagesLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setupSelectedImagesRecyclerView() {
        selectedImagesAdapter = SelectedImagesAdapter(selectedImagesList)
        binding.selectedImagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SelectActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = selectedImagesAdapter
        }
    }

    private fun updateSelectedCount() {
        binding.textViewCountItem.text = selectedImagesList.size.toString()
    }
    private fun checkStoragePermission(): Boolean {
        return storagePer.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun loadImages() {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media.DATA)

        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (it.moveToNext()) {
                val imagePath = it.getString(columnIndex)
                imagePaths.add(imagePath)
            }
            imagePaths.reverse()

            imageAdapter = ImageAdapter(this, imagePaths)
            binding.allImagesRecyclerView.adapter = imageAdapter
        }


    }

}
