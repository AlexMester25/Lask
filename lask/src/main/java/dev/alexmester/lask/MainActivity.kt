package dev.alexmester.lask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.alexmester.lask.main.MainUiState
import dev.alexmester.lask.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value is MainUiState.Loading
        }
        setContent { AppContent(viewModel = viewModel) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onAppResumed()
    }
}

