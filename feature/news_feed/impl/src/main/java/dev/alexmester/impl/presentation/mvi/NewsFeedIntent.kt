package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsCluster

sealed interface NewsFeedIntent {
    data object Refresh : NewsFeedIntent
    data class ArticleClick(val articleId: Long, val articleUrl: String) : NewsFeedIntent

    data class ClustersLoaded(
        val clusters: List<NewsCluster>,
        val country: String,
        val language: String,
        val lastCachedAt: Long?,
    ) : NewsFeedIntent

    data class RefreshSuccess(
        val count: Int,
        val country: String,
        val language: String,
    ) : NewsFeedIntent
    data object RefreshComplete : NewsFeedIntent
    data class RefreshFailure(
        val error: NetworkError,
        val cachedClusters: List<NewsCluster>,
        val lastCachedAt: Long?,
    ) : NewsFeedIntent
}
