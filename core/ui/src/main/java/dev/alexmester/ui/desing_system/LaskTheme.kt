package dev.alexmester.ui.desing_system

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider


@Composable
fun LaskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    typography: LaskTypography = LaskTypography(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val colors = if (darkTheme) LaskDarkColors else LaskLightColors
    CompositionLocalProvider(
        LocalLaskColors provides colors,
        LocalLaskTypography provides typography,
    ) {
        MaterialTheme(
            content = content
        )
    }
}