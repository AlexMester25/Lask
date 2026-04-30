package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.impl.domain.model.FeedResult
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsCluster


object NewsFeedReducer {

    fun reduce(
        clusters: List<NewsCluster>,
        feedResult: FeedResult,
        country: String,
        language: String,
        lastCachedAt: Long?,
    ): NewsFeedState = when (feedResult) {

        is FeedResult.Loading -> when {
            clusters.isNotEmpty() -> NewsFeedState.Content(
                clusters = clusters,
                country = country,
                language = language,
                lastCachedAt = lastCachedAt,
                contentState = ContentState.Refreshing,
            )
            else -> NewsFeedState.Loading
        }

        is FeedResult.Success -> when {
            clusters.isNotEmpty() -> NewsFeedState.Content(
                clusters = clusters,
                country = country,
                language = language,
                lastCachedAt = lastCachedAt,
                contentState = ContentState.Idle,
            )
            else -> NewsFeedState.Empty(country = country, language = language)
        }

        is FeedResult.Error -> when {
            feedResult.error is NetworkError.NoInternet && clusters.isNotEmpty() ->
                NewsFeedState.Content(
                    clusters = clusters,
                    country = country,
                    language = language,
                    lastCachedAt = lastCachedAt,
                    contentState = ContentState.Offline(lastCachedAt),
                )
            clusters.isNotEmpty() -> NewsFeedState.Content(
                clusters = clusters,
                country = country,
                language = language,
                lastCachedAt = lastCachedAt,
                contentState = ContentState.Idle,
            )
            else -> NewsFeedState.Error(errorType = feedResult.error)
        }

        is FeedResult.Idle -> NewsFeedState.Loading
    }
}