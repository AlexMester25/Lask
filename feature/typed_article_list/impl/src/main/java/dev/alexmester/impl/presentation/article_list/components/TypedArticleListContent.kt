package dev.alexmester.impl.presentation.article_list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import dev.alexmester.impl.presentation.article_list.mvi.TypedArticleListIntent
import dev.alexmester.impl.presentation.article_list.mvi.TypedArticleListState
import dev.alexmester.ui.components.list_card.LaskArticleCard
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

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CategoryFilterRow(
                modifier = Modifier,
                categories = state.categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = { category ->
                    onIntent(TypedArticleListIntent.SelectCategory(category))
                },
            )
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = scrollToTopState.contentAlpha },
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = state.filteredArticles,
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
            }
        }

        LaskScrollToTopFab(
            state = scrollToTopState,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
        )
    }
}
