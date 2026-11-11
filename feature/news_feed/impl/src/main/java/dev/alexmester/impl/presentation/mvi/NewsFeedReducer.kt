package dev.alexmester.impl.presentation.mvi

import dev.alexmester.impl.domain.model.FeedLoadState
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsCluster


object NewsFeedReducer {

    fun reduce(
        clusters: List<NewsCluster>,
        loadState: FeedLoadState,
        country: String,
        language: String,
        lastCachedAt: Long?,
    ): NewsFeedState = when (loadState) {

        is FeedLoadState.Loading -> when {
            clusters.isNotEmpty() -> NewsFeedState.Content(
                clusters = clusters,
                country = country,
                language = language,
                lastCachedAt = lastCachedAt,
                contentState = ContentState.Refreshing,
            )
            else -> NewsFeedState.Loading
        }

        is FeedLoadState.Success -> when {
            clusters.isNotEmpty() -> NewsFeedState.Content(
                clusters = clusters,
                country = country,
                language = language,
                lastCachedAt = lastCachedAt,
                contentState = ContentState.Idle,
            )
            else -> NewsFeedState.Empty(country = country, language = language)
        }

        is FeedLoadState.Error -> when {
            loadState.error is NetworkError.NoInternet && clusters.isNotEmpty() ->
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
            else -> NewsFeedState.Error(errorType = loadState.error)
        }

        is FeedLoadState.Idle -> NewsFeedState.Loading
    }
}
