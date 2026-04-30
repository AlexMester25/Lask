package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alexmester.newsfeed.impl.presentation.components.NewsFeedClusterStickyHeader
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedState
import dev.alexmester.ui.components.list_card.LaskArticleCard

@Composable
internal fun NewsFeedList(
    modifier: Modifier = Modifier,
    state: NewsFeedState.Content,
    readArticleIds: Set<Long>,
    bottomPadding: Dp,
    onClickArticle: (articleId: Long, articleUrl: String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 16.dp),
        modifier = modifier.fillMaxSize(),
    ) {
        state.clusters.forEach { cluster ->
            stickyHeader(key = "header_${cluster.id}") {
                NewsFeedClusterStickyHeader(
                    title = cluster.leadArticle.title
                )
            }
            items(
                items = cluster.articles,
                key = { it.id },
            ) { article ->
                LaskArticleCard(
                    modifier = Modifier.animateItem(),
                    article = article,
                    isRead = article.id in readArticleIds,
                    onClick = { onClickArticle(article.id, article.url) }
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(bottomPadding + 64.dp))
        }
    }
}