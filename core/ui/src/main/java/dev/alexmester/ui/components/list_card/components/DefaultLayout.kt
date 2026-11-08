package dev.alexmester.ui.components.list_card.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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

        Box(
            modifier = Modifier
                .animateContentSize()
                .width(if (selectionMode) 40.dp else 0.dp),
            contentAlignment = Alignment.Center
        ) {
            // Кнопка нужна только в режиме выделения (edit mode в закладках). Вне его
            // она измеряется в 0dp, но компонируется на каждой карточке — убираем эту работу.
            if (selectionMode) {
                LaskBookmarkButton(
                    isBookmarked = isKept,
                    onClick = onBookmarkToggle,
                    style = BookmarkButtonStyle.Standalone,
                )
            }
        }
    }
}