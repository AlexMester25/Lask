package dev.alexmester.ui.components.list_card.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.alexmester.models.news.NewsArticle

@Composable
internal fun LeadingLayout(
    modifier: Modifier,
    article: NewsArticle,
    isRead: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ArticleCardImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            imageUrl = article.image,
            title = article.title,
            articleId = article.id,
            isRead = isRead
        )

        ArticleTitle(article)
        ArticleMeta(article)
    }
}