package com.example.selectpic

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.selectpic.databinding.ActivitySelectAlbumBinding
import com.example.selectpic.databinding.DialogExitBinding

class SelectAlbum : BaseActivity() {

    private val binding by lazy { ActivitySelectAlbumBinding.inflate(layoutInflater) }
    private val albumList = mutableListOf<AlbumModel>()
    private lateinit var albumAdapter: AlbumAdapter

    private val storagePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    else
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                loadAlbums()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupRecyclerView()

        if (hasStoragePermissions()) {
            loadAlbums()
        } else {
            permissionLauncher.launch(storagePermissions)
        }

        binding.btnBack.setOnClickListener {
           onBackPressed()
        }


    }

    private fun setupRecyclerView() {
        albumAdapter = AlbumAdapter(this, albumList) { album ->
            val intent = Intent(this, SelectActivity::class.java)
            intent.putExtra("ALBUM_NAME", album.name)
            startActivity(intent)
            finish()
        }


        binding.selectedAlbum.apply {
            layoutManager = GridLayoutManager(this@SelectAlbum, 1)
            adapter = albumAdapter
        }



    }

    private fun loadAlbums() {
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )

        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} IS NOT NULL"
        val albumMap = mutableMapOf<String, AlbumModel>()
        var recentImagesCount = 0
        var recentCoverImagePath: String? = null

        contentResolver.query(uri, projection, selection, null, "${MediaStore.Images.Media.DATE_ADDED} DESC")?.use { cursor ->
            if (cursor.count == 0) {
                Toast.makeText(this, "No albums found", Toast.LENGTH_SHORT).show()
                return
            }
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val pathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val albumName = cursor.getString(nameIndex)
                val coverImagePath = cursor.getString(pathIndex)
                if (recentCoverImagePath == null) {
                    recentCoverImagePath = coverImagePath // First image is used as cover
                }
                recentImagesCount++
                if (!albumMap.containsKey(albumName)) {
                    albumMap[albumName] = AlbumModel(
                        name = albumName,
                        coverImagePath = coverImagePath,
                        numberOfImages = 1
                    )
                } else {
                    albumMap[albumName]?.numberOfImages =
                        (albumMap[albumName]?.numberOfImages ?: 0) + 1
                }
            }
            if (recentImagesCount > 0) {
                albumList.add(
                    0, // Add at the top
                    AlbumModel(
                        name = "Recent",
                        coverImagePath = recentCoverImagePath ?: "",
                        numberOfImages = recentImagesCount
                    )
                )
            }
            albumList.addAll(albumMap.values)
            albumAdapter.notifyDataSetChanged()
        } ?: run {
            Toast.makeText(this, "Failed to load albums", Toast.LENGTH_SHORT).show()
        }
    }


    private fun hasStoragePermissions(): Boolean =
        storagePermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    override fun onBackPressed(){

        val binding2 = DialogExitBinding.inflate(layoutInflater)
        val dialog2 = Dialog(this)
        dialog2.setContentView(binding2.root)
        val window = dialog2.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog2.setCanceledOnTouchOutside(false)
        dialog2.setCancelable(false)
        binding2.btnExit.setOnClickListener{
            dialog2.dismiss()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            super.onBackPressed()
        }
        binding2.btnStay.setOnClickListener{
            dialog2.dismiss()
        }
        dialog2.show()
    }
}
