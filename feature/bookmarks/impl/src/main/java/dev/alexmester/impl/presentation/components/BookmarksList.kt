package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.components.bottom_bar.LocalBottomBarHeight
import dev.alexmester.ui.components.list_card.ArticleCardVariant
import dev.alexmester.ui.components.list_card.LaskArticleCard
import dev.alexmester.ui.components.scroll_to_top.LaskScrollToTopFab
import dev.alexmester.ui.components.scroll_to_top.rememberScrollToTopState

@Composable
internal fun BookmarksList(
    articles: List<NewsArticle>,
    isEditMode: Boolean,
    pendingRemovalIds: Set<Long>,
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
    onTogglePendingRemoval: (articleId: Long) -> Unit,
) {
    val listState = rememberLazyListState()
    val scrollToTopState = rememberScrollToTopState(listState)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = scrollToTopState.contentAlpha },
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

        LaskScrollToTopFab(
            state = scrollToTopState,
            visible = !isEditMode,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 16.dp,
                    bottom = LocalBottomBarHeight.current + 16.dp,
                ),
        )
    }
}
