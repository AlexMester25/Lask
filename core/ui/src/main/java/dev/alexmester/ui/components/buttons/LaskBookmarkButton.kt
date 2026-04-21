package dev.alexmester.ui.components.buttons

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette

enum class BookmarkButtonStyle {
    IconBottomBar,
    Standalone
}

@Composable
fun LaskBookmarkButton(
    isBookmarked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: BookmarkButtonStyle = BookmarkButtonStyle.IconBottomBar,
) {
    val tint by animateColorAsState(
        targetValue = if (isBookmarked) LaskPalette.Bookmark
        else MaterialTheme.LaskColors.textPrimary,
        label = "bookmarkColor",
    )
    val icon = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder

    when (style) {
        BookmarkButtonStyle.IconBottomBar ->
            IconButton(
                onClick = onClick,
                modifier = modifier
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint
                )
            }

        BookmarkButtonStyle.Standalone ->
            IconButton(
                modifier = modifier.size(30.dp),
                onClick = onClick,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(24.dp)
                )
            }
    }
}