package dev.alexmester.impl.presentation.article_list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.article_list.mvi.TypedArticleListIntent
import dev.alexmester.impl.presentation.article_list.mvi.TypedArticleListState
import dev.alexmester.ui.R
import dev.alexmester.ui.components.list_card.LaskArticleCard
import dev.alexmester.ui.components.notification_screen.LaskNotificationScreen
import dev.alexmester.ui.components.notification_screen.NotificationType
import dev.alexmester.ui.components.pagination.LaskLoadMoreEffect
import dev.alexmester.ui.components.pagination.LaskPaginationLoading
import dev.alexmester.ui.components.scroll_to_top.LaskScrollToTopFab
import dev.alexmester.ui.components.scroll_to_top.rememberScrollToTopState

@Composable
fun TypedArticleListContent(
    modifier: Modifier = Modifier,
    state: TypedArticleListState,
    onIntent: (TypedArticleListIntent) -> Unit,
) {
    val listState = rememberLazyListState()
    val scrollToTopState = rememberScrollToTopState(listState)

    // Смена категории — это другой список, поэтому возвращаемся к его началу.
    // Рост окна фильтр не меняет, так что позиция при догрузке сохраняется.
    LaunchedEffect(state.selectedFilter) {
        listState.scrollToItem(0)
    }

    LaskLoadMoreEffect(
        listState = listState,
        canLoadMore = state.canLoadMore,
        isLoadingMore = state.isLoadingMore,
        itemCount = state.articles.size,
        onLoadMore = { onIntent(TypedArticleListIntent.LoadMore) },
    )

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Если история пуста совсем, чипы не нужны: остаётся только «All (0)».
            if (state.categories.size > 1) {
                CategoryFilterRow(
                    modifier = Modifier,
                    categories = state.categories,
                    selectedFilter = state.selectedFilter,
                    onFilterSelected = { filter ->
                        onIntent(TypedArticleListIntent.SelectFilter(filter))
                    },
                )
            }

            if (state.isEmpty) {
                LaskNotificationScreen(
                    type = NotificationType.Warning(
                        text = stringResource(
                            if (state.isFilteredEmpty) {
                                R.string.warning_no_articles_in_category
                            } else {
                                R.string.warning_no_articles
                            }
                        ),
                        image = Icons.Default.Article,
                    )
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = scrollToTopState.contentAlpha },
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = state.articles,
                        key = { it.id },
                    ) { article ->
                        LaskArticleCard(
                            modifier = Modifier.animateItem(),
                            article = article,
                            onClick = {
                                onIntent(
                                    TypedArticleListIntent.ArticleClick(
                                        articleId = article.id,
                                        articleUrl = article.url,
                                    )
                                )
                            },
                        )
                    }

                    if (state.isLoadingMore) {
                        item(key = "typed_list_pagination_loading") {
                            LaskPaginationLoading()
                        }
                    }
                }
            }
        }

        LaskScrollToTopFab(
            state = scrollToTopState,
            visible = state.articles.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
        )
    }
}
