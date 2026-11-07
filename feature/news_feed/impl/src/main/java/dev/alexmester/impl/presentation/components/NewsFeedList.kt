package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import dev.alexmester.newsfeed.impl.presentation.components.NewsFeedClusterStickyHeader
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedState
import dev.alexmester.ui.components.bottom_bar.LocalBottomBarHeight
import dev.alexmester.ui.components.list_card.LaskArticleCard

@Composable
internal fun NewsFeedList(
    modifier: Modifier = Modifier,
    state: NewsFeedState.Content,
    readArticleIds: Set<Long>,
    onClickArticle: (articleId: Long, articleUrl: String) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = LocalBottomBarHeight.current),
        modifier = modifier
            .fillMaxSize()
            .testTag("feed_list"),
    ) {
        state.clusters.forEach { cluster ->
            stickyHeader(
                key = "header_${cluster.id}",
                contentType = "header",
            ) {
                NewsFeedClusterStickyHeader(
                    title = cluster.leadArticle.title
                )
            }
            items(
                items = cluster.articles,
                key = { it.id },
                contentType = { "article" },
            ) { article ->
                val onClick = remember(article.id, article.url, onClickArticle) {
                    { onClickArticle(article.id, article.url) }
                }
                LaskArticleCard(
                    modifier = Modifier
                        .animateItem()
                        .testTag("article_card"),
                    article = article,
                    isRead = article.id in readArticleIds,
                    onClick = onClick
                )
            }
        }
    }
}