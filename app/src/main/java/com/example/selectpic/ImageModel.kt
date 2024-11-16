package com.example.selectpic

data class ImageModel(
    val id: Long,
    val dateTaken: Long,
    val fileName: String,
    val filePath: String,
    val album: String
)
