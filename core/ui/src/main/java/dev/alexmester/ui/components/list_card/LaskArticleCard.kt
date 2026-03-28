package dev.alexmester.ui.components.list_card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTypography


@Composable
fun LaskArticleCard(
    modifier: Modifier = Modifier,
    title: String,
    imageUrl: String?,
    category: String?,
    publishDate: String,
    authors: List<String?>,
    sentiment: Double?,
    selectionMode: Boolean = false,
    isKept: Boolean = true,
    onBookmarkToggle: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = title,
                style = MaterialTheme.LaskTypography.h5,
                color = MaterialTheme.LaskColors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                category?.let {
                    Text(
                        text = it.replaceFirstChar { c -> c.uppercase() },
                        style = MaterialTheme.LaskTypography.footnote,
                        color = MaterialTheme.LaskColors.informative,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    sentiment?.let { SentimentDot(it) }
                    Text(
                        text = publishDate,
                        style = MaterialTheme.LaskTypography.footnote,
                        color = MaterialTheme.LaskColors.textSecondary,
                    )
                    authors.firstOrNull()?.let { author ->
                        Text(
                            text = "· $author",
                            style = MaterialTheme.LaskTypography.footnote,
                            color = MaterialTheme.LaskColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }

        ArticleThumbnail(imageUrl = imageUrl, title = title)

        AnimatedVisibility(
            visible = selectionMode,
            enter = fadeIn() + scaleIn(initialScale = 0.7f),
            exit  = fadeOut() + scaleOut(targetScale = 0.7f),
        ) {
            BookmarkIcon(
                isKept = isKept,
                onToggle = onBookmarkToggle,
            )
        }
    }
}


@Composable
private fun ArticleThumbnail(
    imageUrl: String?,
    title: String
) {
    val sizeMod = Modifier
        .width(112.dp)
        .height(80.dp)
        .clip(RoundedCornerShape(8.dp))

    if (imageUrl != null) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = sizeMod,
            loading = { ThumbnailPlaceholder() },
            error   = { ThumbnailPlaceholder() },
        )
    } else {
        ThumbnailPlaceholder(modifier = sizeMod)
    }
}

@Composable
private fun ThumbnailPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.LaskColors.backgroundSecondary),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = null,
            tint = MaterialTheme.LaskColors.textSecondary.copy(alpha = 0.4f),
            modifier = Modifier.size(28.dp),
        )
    }
}

@Composable
private fun BookmarkIcon(
    isKept: Boolean,
    onToggle: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isKept) 1f else 0.85f,
        animationSpec = spring(dampingRatio = 0.6f),
    )

    Icon(
        imageVector = if (isKept) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
        contentDescription = if (isKept) "Will be kept" else "Will be removed",
        tint = LaskPalette.Bookmark,
        modifier = Modifier
            .size(24.dp)
            .graphicsLayer{
                scaleX = scale
                scaleY = scale
            }
            .clickable(onClick = onToggle),
    )
}

@Composable
private fun SentimentDot(sentiment: Double) {
    val color = when {
        sentiment > 0.1  -> LaskPalette.Sentiment_Positive
        sentiment < -0.1 -> LaskPalette.Sentiment_Negative
        else             -> LaskPalette.Sentiment_Neutral
    }
    Text(
        text = "●",
        color = color,
        style = MaterialTheme.LaskTypography.footnote,
    )
}