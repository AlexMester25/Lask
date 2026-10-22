package dev.alexmester.lask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dev.alexmester.lask.splash_screen.SplashState
import dev.alexmester.lask.splash_screen.SplashViewModel
import dev.alexmester.platform.locale.DeviceLocaleSynchronizer
import dev.alexmester.ui.desing_system.LaskPalette
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModel()
    private val localeSynchronizer: DeviceLocaleSynchronizer by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = LaskPalette.BackgroundPrimaryDark.toArgb(),
                darkScrim = LaskPalette.BackgroundPrimaryLight.toArgb()
            )
        )
        splashScreen.setKeepOnScreenCondition {
            splashViewModel.state.value is SplashState.Loading
        }
        setContent {
            AppContent(splashViewModel = splashViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch { localeSynchronizer.syncIfNeeded() }
    }
}

