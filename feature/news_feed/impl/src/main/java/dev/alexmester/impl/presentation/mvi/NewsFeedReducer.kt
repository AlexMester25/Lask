package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.error.NetworkErrorUiMapper
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsCluster
import dev.alexmester.ui.uitext.UiText

object NewsFeedReducer {

    fun reduce(state: NewsFeedScreenState, intent: NewsFeedIntent): NewsFeedScreenState =
        when (intent) {
            is NewsFeedIntent.Refresh -> when (state) {
                is NewsFeedScreenState.Content -> state.copy(
                    contentState = ContentState.Refreshing,
                )
                is NewsFeedScreenState.Error -> state.copy(isRefreshing = true)
                is NewsFeedScreenState.Empty -> state.copy(isRefreshing = true)
                else -> state
            }
            else -> state
        }

    fun onClustersLoaded(
        clusters: List<NewsCluster>,
        lastCachedAt: Long?,
        country: String,
    ): NewsFeedScreenState =
        NewsFeedScreenState.Content(
            clusters = clusters,
            country = country,
            lastCachedAt = lastCachedAt,
            contentState = ContentState.Idle,
        )

    fun onEmpty(country: String, language: String): NewsFeedScreenState =
        NewsFeedScreenState.Empty(country = country, language = language, isRefreshing = false)

    fun onNetworkError(
        state: NewsFeedScreenState,
        error: NetworkError,
        cachedClusters: List<NewsCluster>,
        lastCachedAt: Long?,
    ): Pair<NewsFeedScreenState, UiText> {
        val message = NetworkErrorUiMapper.toUiText(error)

        val newState = when {
            error is NetworkError.NoInternet && cachedClusters.isNotEmpty() ->
                NewsFeedScreenState.Content(
                    clusters = cachedClusters,
                    lastCachedAt = lastCachedAt,
                    contentState = ContentState.Offline(lastCachedAt),
                )

            state is NewsFeedScreenState.Content ->
                state.copy(contentState = ContentState.Idle)

            else -> NewsFeedScreenState.Error(
                message = message,
                isRefreshing = false,
            )
        }

        return newState to message
    }
}