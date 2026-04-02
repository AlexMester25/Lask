package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.mvi.ArticleDetailIntent
import dev.alexmester.ui.desing_system.LaskColors
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.rememberHazeState

@Composable
fun ArticleDetailBottomBar(
    isBookmarked: Boolean,
    clapCount: Int,
    isClapAnimating: Boolean,
    onIntent: (ArticleDetailIntent) -> Unit,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.LaskColors.brand_blue10)
            .navigationBarsPadding()
            .hazeEffect(state = hazeState) {
                alpha = 0.5f
                blurRadius = 20.dp
                noiseFactor = 0.05f
            }
            .padding(horizontal = 32.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onIntent(ArticleDetailIntent.Back) }) {
            Icon(
                imageVector = Icons.Default.KeyboardBackspace,
                contentDescription = null,
                tint = MaterialTheme.LaskColors.textPrimary,
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClapButton(
                count = clapCount,
                isAnimating = isClapAnimating,
                onClick = { onIntent(ArticleDetailIntent.Clap) },
            )

            BookmarkButton(
                isBookmarked = isBookmarked,
                onClick = { onIntent(ArticleDetailIntent.ToggleBookmark) },
            )

            IconButton(onClick = { onIntent(ArticleDetailIntent.Share) }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = MaterialTheme.LaskColors.textPrimary,
                )
            }
        }
    }
}