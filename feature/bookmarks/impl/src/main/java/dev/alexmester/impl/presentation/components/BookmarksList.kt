package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.components.list_card.ArticleCardVariant
import dev.alexmester.ui.components.list_card.LaskArticleCard

@Composable
internal fun BookmarksList(
    articles: List<NewsArticle>,
    isEditMode: Boolean,
    pendingRemovalIds: Set<Long>,
    bottomPadding: Dp,
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
    onTogglePendingRemoval: (articleId: Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
    ) {
        items(
            items = articles,
            key = { it.id },
        ) { article ->
            val isKept = article.id !in pendingRemovalIds
            LaskArticleCard(
                modifier = Modifier.animateItem(),
                article = article,
                selectionMode = isEditMode,
                variant = ArticleCardVariant.Default,
                isKept = isKept,
                onClick = { onArticleClick(article.id, article.url) },
                onBookmarkToggle = { onTogglePendingRemoval(article.id) },
            )
        }
        item {
            Spacer(modifier = Modifier.height(bottomPadding + 32.dp))
        }
    }
}