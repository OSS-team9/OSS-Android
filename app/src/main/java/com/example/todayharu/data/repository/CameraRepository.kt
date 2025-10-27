package com.example.todayharu.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraRepository(private val context: Context) {

    fun createImageUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFile = File(context.cacheDir, "JPEG_${timeStamp}_.jpg")

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // AndroidManifest의 authorities와 일치
            imageFile
        )
    }
}