package com.example.todayharu.ui.camera

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImagePreviewScreen(
    imageUri: Uri,
    viewModel: CameraViewModel,
    onDismiss: () -> Unit // 닫기 버튼 콜백
) {
    // 1. ViewModel에 프리뷰가 나타났음을 알림
    LaunchedEffect(key1 = Unit) {
        viewModel.onPreviewAppeared(imageUri)
    }

    // 2. ViewModel의 메시 상태를 구독
    val meshState by viewModel.meshUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            viewModel.onPreviewDismissed() // ★ 상태 초기화
            onDismiss()
        }) {
            Text("닫기")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = meshState) {
                is MeshUiState.Loading -> {
                    CircularProgressIndicator() // 로딩 중
                }
                is MeshUiState.Error -> {
                    Text(state.message) // 에러 메시지
                }
                is MeshUiState.Success -> {
                    // ✅ 메시가 그려진 비트맵을 Coil로 바로 로드
                    AsyncImage(
                        model = state.processedBitmap,
                        contentDescription = "메시가 적용된 사진",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit // 화면 비율에 맞게
                    )
                }
            }
        }
    }
}
