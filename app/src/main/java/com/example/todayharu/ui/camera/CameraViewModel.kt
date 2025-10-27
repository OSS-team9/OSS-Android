package com.example.todayharu.ui.camera

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todayharu.data.repository.CameraRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CameraViewModel(private val cameraRepository: CameraRepository) : ViewModel() {

    // View에 '카메라 실행' 이벤트를 전달
    private val _launchCameraEvent = MutableSharedFlow<Uri>()
    val launchCameraEvent = _launchCameraEvent.asSharedFlow()

    // View에 '찍은 사진 확인' 이벤트를 전달
    private val _navigateToImageDetailEvent = MutableSharedFlow<Uri>()
    val navigateToImageDetailEvent = _navigateToImageDetailEvent.asSharedFlow()

    private var tempUriToSavePhoto: Uri? = null

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
}

class CameraViewModelFactory(private val repository: CameraRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}