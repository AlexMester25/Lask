package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.mvi.ExploreIntent
import dev.alexmester.impl.presentation.mvi.ExploreState
import dev.alexmester.ui.R
import dev.alexmester.ui.components.bottom_bar.LocalBottomBarHeight
import dev.alexmester.ui.components.list_card.ArticleCardVariant
import dev.alexmester.ui.components.list_card.LaskArticleCard
import dev.alexmester.ui.components.pagination.LaskPaginationError
import dev.alexmester.ui.components.pagination.LaskPaginationLoading
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
internal fun ExploreList(
    state: ExploreState.Content,
    readArticleIds: Set<Long>,
    onIntent: (ExploreIntent) -> Unit,
) {
    val listState = rememberLazyListState()

    val shouldLoadMore = remember {
        derivedStateOf {
            if (state.endReached || state.isLoadingMore || state.loadMoreError) return@derivedStateOf false
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleIndex >= totalItems - 4
        }
    }

    LaunchedEffect(shouldLoadMore.value, state.refreshId) {
        if (shouldLoadMore.value && !state.loadMoreError) {
            onIntent(ExploreIntent.LoadMore)
        }
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = LocalBottomBarHeight.current),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                text = stringResource(R.string.interests_just_for_you),
                style = MaterialTheme.LaskTypography.h4,
                color = MaterialTheme.LaskColors.textPrimary,
            )
        }
        itemsIndexed(
            items = state.articles,
            key = { _, item -> item.id },
        ) { index, article ->
            LaskArticleCard(
                modifier = Modifier.animateItem(),
                article = article,
                variant = if (index == 0) ArticleCardVariant.Leading else ArticleCardVariant.Default,
                isRead = article.id in readArticleIds,
                onClick = {
                    onIntent(ExploreIntent.ArticleClick(article.id, article.url))
                }
            )
        }
        item {
            when {
                state.isLoadingMore -> LaskPaginationLoading()
                state.loadMoreError -> LaskPaginationError(
                    onRetry = { onIntent(ExploreIntent.RetryLoadMore) }
                )
                else -> Unit
            }
        }
    }
}