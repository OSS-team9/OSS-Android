package com.example.todayharu.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.todayharu.ui.camera.CameraViewModel

@Composable
fun MainScreen(viewModel: CameraViewModel) {
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            // 결과를 ViewModel에 알림
            viewModel.onPictureTaken(success)
        }
    )

    LaunchedEffect(Unit) {
        viewModel.launchCameraEvent.collect { uriToLaunch ->
            // 이벤트가 발생하면 Launcher 실행
            cameraLauncher.launch(uriToLaunch)
        }
    }

    // 3. UI (버튼만 존재)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            viewModel.onTakePhotoClick()
        }) {
            Text("카메라 열기")
        }
    }
}