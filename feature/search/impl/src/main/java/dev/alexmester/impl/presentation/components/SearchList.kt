package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.mvi.SearchIntent
import dev.alexmester.impl.presentation.mvi.SearchState
import dev.alexmester.ui.components.list_card.LaskArticleCard
import dev.alexmester.ui.components.pagination.LaskPaginationError
import dev.alexmester.ui.components.pagination.LaskPaginationLoading

@Composable
fun SearchList(
    modifier: Modifier = Modifier,
    state: SearchState,
    readArticleIds: Set<Long>,
    onIntent: (SearchIntent) -> Unit,
) {
    val listState = rememberLazyListState()

    val shouldLoadMore = remember {
        derivedStateOf {
            if (state.endReached || state.isLoadingMore || state.loadMoreError || state.isLoading)
                return@derivedStateOf false
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !state.loadMoreError) {
            onIntent(SearchIntent.LoadMore)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
    ) {
        items(state.results, key = { "${it.id}-${it.publishDate}" }) { article ->
            LaskArticleCard(
                modifier = Modifier.fillMaxWidth(),
                article = article,
                isRead = article.id in readArticleIds,
                onClick = {
                    onIntent(SearchIntent.ArticleClick(article.id, article.url))
                },
            )
        }

        item {
            when {
                state.isLoadingMore -> {
                    LaskPaginationLoading()
                    Spacer(modifier = Modifier.height(64.dp))
                }
                state.loadMoreError -> {
                    LaskPaginationError(
                        onRetry = { onIntent(SearchIntent.RetryLoadMore) }
                    )
                    Spacer(modifier = Modifier.height(64.dp))
                }
                else -> {
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
}