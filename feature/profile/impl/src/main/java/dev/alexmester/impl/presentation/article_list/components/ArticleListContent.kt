package dev.alexmester.impl.presentation.article_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListIntent
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListState
import dev.alexmester.ui.components.list_card.LaskArticleCard

@Composable
fun ArticleListContent(
    modifier: Modifier = Modifier,
    state: ArticleListState,
    onIntent: (ArticleListIntent) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        CategoryFilterRow(
            modifier = Modifier,
            categories = state.categories,
            selectedCategory = state.selectedCategory,
            onCategorySelected = { category ->
                onIntent(ArticleListIntent.SelectCategory(category))
            },
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                items = state.filteredArticles,
                key = { it.id },
            ) { article ->
                LaskArticleCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(),
                    article = article,
                    onClick = {
                        onIntent(
                            ArticleListIntent.ArticleClick(
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