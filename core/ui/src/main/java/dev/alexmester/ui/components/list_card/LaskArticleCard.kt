package dev.alexmester.ui.components.list_card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.components.buttons.BookmarkButtonStyle
import dev.alexmester.ui.components.buttons.LaskBookmarkButton
import dev.alexmester.ui.components.common.LaskSentimentDot
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.ui.transition.sharedElementIfAvailable
import dev.alexmester.utils.DateFormatter

@Composable
fun LaskArticleCard(
    modifier: Modifier = Modifier,
    article: NewsArticle,
    selectionMode: Boolean = false,
    isKept: Boolean = true,
    isRead: Boolean = false,
    onBookmarkToggle: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .height(IntrinsicSize.Min)
            .animateContentSize(),
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
                text = article.title,
                style = MaterialTheme.LaskTypography.h5,
                color = MaterialTheme.LaskColors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .sharedElementIfAvailable(key = "title_${article.id}")
                    .padding(bottom = 8.dp),
            )
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                article.category?.let {
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
                    article.sentiment?.let { LaskSentimentDot(it) }
                    Text(
                        text = DateFormatter.formatPublishDate(article.publishDate),
                        style = MaterialTheme.LaskTypography.footnote,
                        color = MaterialTheme.LaskColors.textSecondary,
                    )
                    article.authors.firstOrNull()?.let { author ->
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

        LaskArticleCardImage(
            imageUrl = article.image,
            title = article.title,
            articleId = article.id,
            isRead = isRead
        )

        AnimatedVisibility(
            visible = selectionMode,
            enter = fadeIn() + scaleIn(initialScale = 0.7f)
                    + slideInHorizontally(initialOffsetX = { it / 2 }),
            exit = fadeOut() + scaleOut(targetScale = 0.7f)
                    + slideOutHorizontally(targetOffsetX = { it / 2 }),
        ) {
            LaskBookmarkButton(
                isBookmarked = isKept,
                onClick = onBookmarkToggle,
                style = BookmarkButtonStyle.Standalone,
            )
        }
    }
}







