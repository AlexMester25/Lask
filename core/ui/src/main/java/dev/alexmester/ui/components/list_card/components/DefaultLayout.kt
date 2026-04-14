package dev.alexmester.ui.components.list_card.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.components.buttons.BookmarkButtonStyle
import dev.alexmester.ui.components.buttons.LaskBookmarkButton

@Composable
internal fun DefaultLayout(
    modifier: Modifier,
    article: NewsArticle,
    isRead: Boolean,
    selectionMode: Boolean,
    isKept: Boolean,
    onBookmarkToggle: () -> Unit,
    onClick: () -> Unit,
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
            ArticleTitle(article)
            ArticleMeta(article)
        }

        ArticleCardImage(
            modifier = Modifier
                .width(112.dp)
                .height(80.dp),
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