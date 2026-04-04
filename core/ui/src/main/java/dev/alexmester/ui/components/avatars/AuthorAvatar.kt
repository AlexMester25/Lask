package dev.alexmester.ui.components.avatars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun AuthorAvatar(
    modifier: Modifier = Modifier,
    char: String,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.LaskColors.brand_blue10),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = char,
            style = MaterialTheme.LaskTypography.body2SemiBold,
            color = MaterialTheme.LaskColors.brand_blue,
        )
    }
}