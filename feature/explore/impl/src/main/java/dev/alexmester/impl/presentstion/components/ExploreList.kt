package dev.alexmester.impl.presentstion.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alexmester.impl.presentstion.mvi.ExploreIntent
import dev.alexmester.impl.presentstion.mvi.ExploreState
import dev.alexmester.ui.R
import dev.alexmester.ui.components.list_card.ArticleCardVariant
import dev.alexmester.ui.components.list_card.LaskArticleCard
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun ExploreList(
    state: ExploreState.Content,
    readArticleIds: Set<Long>,
    bottomPadding: Dp,
    onIntent: (ExploreIntent) -> Unit,
) {

    val lastArticle = state.articles.last()

    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp,vertical = 16.dp),
                text = stringResource(R.string.interests_just_for_you),
                style = MaterialTheme.LaskTypography.h4,
                color = MaterialTheme.LaskColors.textPrimary,
            )
        }
        itemsIndexed(
            items = state.articles,
            key = { _, item -> item.id },
        ) { index, article ->
            val isLast = article == lastArticle
            LaskArticleCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isLast) 16.dp else 0.dp),
                article = article,
                variant = if (index == 0) ArticleCardVariant.Leading else ArticleCardVariant.Default,
                isRead = article.id in readArticleIds,
                onClick = {
                    onIntent(ExploreIntent.ArticleClick(article.id, article.url))
                }
            )

            if (index >= state.articles.lastIndex - 3) {
                onIntent(ExploreIntent.LoadMore)
            }
        }

        if (state.isLoadingMore) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp).size(24.dp),
                        color = MaterialTheme.LaskColors.brand_blue10,
                        trackColor = MaterialTheme.LaskColors.brand_blue,
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(bottomPadding + 32.dp))
        }
    }
}