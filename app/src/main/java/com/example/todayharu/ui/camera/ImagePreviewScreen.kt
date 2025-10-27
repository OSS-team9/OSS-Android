package com.example.todayharu.ui.camera

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImagePreviewScreen(
    imageUri: Uri,
    onDismiss: () -> Unit // 닫기 버튼 콜백
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onDismiss) {
            Text("닫기")
        }

        Spacer(modifier = Modifier.height(20.dp))

        AsyncImage(
            model = imageUri,
            contentDescription = "찍은 사진",
            modifier = Modifier.fillMaxSize()
        )
    }
}