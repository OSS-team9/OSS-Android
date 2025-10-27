package com.example.todayharu

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todayharu.data.repository.CameraRepository
import com.example.todayharu.ui.MainScreen
import com.example.todayharu.ui.camera.CameraViewModel
import com.example.todayharu.ui.camera.CameraViewModelFactory
import com.example.todayharu.ui.camera.ImagePreviewScreen
import com.example.todayharu.ui.theme.TodayHaruTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodayHaruTheme {

                val viewModel: CameraViewModel = viewModel(
                    factory = CameraViewModelFactory(
                        CameraRepository(LocalContext.current.applicationContext)
                    )
                )

                var previewImageUri by remember {
                    mutableStateOf<Uri?>(null)
                }

                LaunchedEffect(Unit) {
                    viewModel.navigateToImageDetailEvent.collect { uri ->
                        previewImageUri = uri
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (previewImageUri == null) {
                        MainScreen(viewModel = viewModel)
                    } else {
                        ImagePreviewScreen(
                            imageUri = previewImageUri!!,
                            onDismiss = {
                                previewImageUri = null
                            }
                        )
                    }
                }
            }
        }
    }
}
