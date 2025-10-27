package com.example.todayharu.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.facemesh.FaceMesh
import com.google.mlkit.vision.facemesh.FaceMeshDetection
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions
import kotlinx.coroutines.tasks.await
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

    suspend fun detectFaceMesh(uri: Uri): List<FaceMesh> {
        // 1. ML Kit 감지기(Detector) 인스턴스 생성
        val options = FaceMeshDetectorOptions.Builder()
            .setUseCase(FaceMeshDetectorOptions.FACE_MESH)
            .build()
        val detector = FaceMeshDetection.getClient(options)

        // 2. Uri를 ML Kit이 이해할 수 있는 InputImage로 변환
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        // 3. 이미지 처리 (await()을 사용해 비동기 작업을 동기식으로 대기)
        return try {
            detector.process(image).await()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}