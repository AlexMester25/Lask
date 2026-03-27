package dev.alexmester.ui.components.bottom_bar

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomTab(
    val icon: ImageVector,
    val title: String,
    val route: Any
)