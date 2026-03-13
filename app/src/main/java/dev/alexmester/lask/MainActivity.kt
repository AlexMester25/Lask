package dev.alexmester.lask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
//            statusBarStyle = SystemBarStyle.auto(
//                lightScrim = LaskPalette.Brand_BlueLight10.toArgb(),
//                darkScrim  = LaskPalette.Brand_BlueDark10.toArgb(),
//            )
        )


        setContent {
            LaskTheme {
                MainScreen()
            }
        }
    }
}

