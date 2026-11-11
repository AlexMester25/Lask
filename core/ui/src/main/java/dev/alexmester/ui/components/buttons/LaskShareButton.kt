package dev.alexmester.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.alexmester.ui.design_system.LaskColors

@Composable
fun LaskShareButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            modifier = modifier,
            imageVector = Icons.Default.Share,
            contentDescription = null,
            tint = MaterialTheme.LaskColors.textPrimary,
        )
    }
}