package com.example.todayharu.ui.camera

import android.graphics.Bitmap

sealed interface MeshUiState {
    object Loading : MeshUiState
    data class Success(val processedBitmap: Bitmap) : MeshUiState
    data class Error(val message: String) : MeshUiState
}