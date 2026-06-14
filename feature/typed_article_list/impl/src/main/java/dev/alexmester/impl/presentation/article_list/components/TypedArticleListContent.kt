package dev.alexmester.impl.presentation.article_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.article_list.mvi.TypedArticleListIntent
import dev.alexmester.impl.presentation.article_list.mvi.TypedArticleListState
import dev.alexmester.ui.components.list_card.LaskArticleCard

@Composable
fun TypedArticleListContent(
    modifier: Modifier = Modifier,
    state: TypedArticleListState,
    onIntent: (TypedArticleListIntent) -> Unit,
) {
    Column(
        modifier = modifier
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
            modifier = Modifier.fillMaxSize(),
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
}