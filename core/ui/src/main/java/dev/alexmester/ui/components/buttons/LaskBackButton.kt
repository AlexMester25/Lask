package dev.alexmester.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.alexmester.ui.design_system.LaskColors

@Composable
fun LaskBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Default.KeyboardBackspace,
            contentDescription = null,
            tint = MaterialTheme.LaskColors.textPrimary,
        )
    }
}