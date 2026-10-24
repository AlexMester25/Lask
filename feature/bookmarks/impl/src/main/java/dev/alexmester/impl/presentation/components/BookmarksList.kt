package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.components.bottom_bar.LocalBottomBarHeight
import dev.alexmester.ui.components.list_card.ArticleCardVariant
import dev.alexmester.ui.components.list_card.LaskArticleCard

@Composable
internal fun BookmarksList(
    articles: List<NewsArticle>,
    isEditMode: Boolean,
    pendingRemovalIds: Set<Long>,
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
    onTogglePendingRemoval: (articleId: Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = LocalBottomBarHeight.current),
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
    }
}