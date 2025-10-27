package com.example.todayharu.ui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todayharu.data.repository.CameraRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.os.Build
import android.provider.MediaStore
import com.google.mlkit.vision.facemesh.FaceMesh

class CameraViewModel(
    private val cameraRepository: CameraRepository,
    private val context: Context
) : ViewModel() {

    // View에 '카메라 실행' 이벤트를 전달
    private val _launchCameraEvent = MutableSharedFlow<Uri>()
    val launchCameraEvent = _launchCameraEvent.asSharedFlow()

    // View에 '찍은 사진 확인' 이벤트를 전달
    private val _navigateToImageDetailEvent = MutableSharedFlow<Uri>()
    val navigateToImageDetailEvent = _navigateToImageDetailEvent.asSharedFlow()

    private var tempUriToSavePhoto: Uri? = null

    private val _meshUiState = MutableStateFlow<MeshUiState>(MeshUiState.Loading)
    val meshUiState = _meshUiState.asStateFlow()

    private val meshPaint = Paint().apply {
        color = Color.CYAN
        strokeWidth = 3f // 선 굵기
        style = Paint.Style.STROKE
    }

    /**
     * View (MainScreen)에서 "카메라 열기" 버튼을 클릭했을 때 호출
     */
    fun onTakePhotoClick() {
        viewModelScope.launch {
            val newUri = cameraRepository.createImageUri()
            tempUriToSavePhoto = newUri // 촬영 성공 시 사용될 Uri 임시 저장
            _launchCameraEvent.emit(newUri) // View에 카메라 실행 요청
        }
    }

    /**
     * View의 ActivityResultLauncher가 결과를 반환했을 때 호출
     */
    fun onPictureTaken(success: Boolean) {
        if (success) {
            // 촬영에 성공하면, 해당 Uri를 가지고 ImageDetailScreen으로 이동하도록 이벤트 발행
            tempUriToSavePhoto?.let { uri ->
                viewModelScope.launch {
                    _navigateToImageDetailEvent.emit(uri)
                }
            }
        }
        tempUriToSavePhoto = null // 사용 후 초기화
    }

    /**
     * ImagePreviewScreen이 나타났을 때 호출될 함수
     */
    fun onPreviewAppeared(uri: Uri) {
        viewModelScope.launch {
            _meshUiState.value = MeshUiState.Loading
            try {
                // 1. Uri에서 원본 비트맵 로드
                val originalBitmap = loadBitmapFromUri(uri)
                // 2. 비트맵을 복사하여 그릴 준비 (수정 가능한 비트맵)
                val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
                // 3. 비트맵 위에 그릴 캔버스 생성
                val canvas = Canvas(mutableBitmap)

                // 4. ML Kit으로 메시 감지
                val meshResult = cameraRepository.detectFaceMesh(uri)

                if (meshResult.isNotEmpty()) {
                    // 5. 비트맵 캔버스에 메시 그리기 (좌표 1:1 매칭)
                    drawMeshOnCanvas(canvas, meshResult)
                    // 6. 완성된 비트맵을 UI 상태로 전달
                    _meshUiState.value = MeshUiState.Success(mutableBitmap)
                } else {
                    _meshUiState.value = MeshUiState.Error("얼굴을 감지하지 못했습니다.")
                }
            } catch (e: Exception) {
                _meshUiState.value = MeshUiState.Error(e.message ?: "알 수 없는 오류")
            }
        }
    }

    /**
     * ImagePreviewScreen이 닫힐 때 StateFlow 초기화
     */
    fun onPreviewDismissed() {
        _meshUiState.value = MeshUiState.Loading
    }

    private fun drawMeshOnCanvas(canvas: Canvas, meshes: List<FaceMesh>) {
        meshes.forEach { faceMesh ->
            faceMesh.allTriangles.forEach { triangle ->
                val points = triangle.allPoints
                if (points.size == 3) {
                    val p1 = points[0].position
                    val p2 = points[1].position
                    val p3 = points[2].position

                    // 원본 비트맵 좌표계에 바로 그리기 (좌표 변환 불필요)
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, meshPaint)
                    canvas.drawLine(p2.x, p2.y, p3.x, p3.y, meshPaint)
                    canvas.drawLine(p3.x, p3.y, p1.x, p1.y, meshPaint)
                }
            }
        }
    }

    // Uri에서 비트맵을 로드하는 헬퍼 함수
    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }
}

class CameraViewModelFactory(
    private val repository: CameraRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}