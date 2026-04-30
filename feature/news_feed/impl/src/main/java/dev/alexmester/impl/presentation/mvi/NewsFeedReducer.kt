package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.error.NetworkError

object NewsFeedReducer {

    fun reduce(state: NewsFeedState, intent: NewsFeedIntent): NewsFeedState = when (intent) {

        is NewsFeedIntent.Refresh -> when (state) {
            is NewsFeedState.Content -> state.copy(contentState = ContentState.Refreshing)
            is NewsFeedState.Error -> state.copy(isRefreshing = true)
            is NewsFeedState.Empty -> state.copy(isRefreshing = true)
            else -> state
        }

        is NewsFeedIntent.ClustersLoaded -> NewsFeedState.Content(
            clusters = intent.clusters,
            country = intent.country,
            language = intent.language,
            lastCachedAt = intent.lastCachedAt,
            contentState = ContentState.Idle,
        )

        is NewsFeedIntent.RefreshSuccess -> when {
            intent.count == 0 && state.contentOrNull?.clusters.isNullOrEmpty() ->
                NewsFeedState.Empty(
                    country = intent.country,
                    language = intent.language,
                    isRefreshing = false,
                )
            else -> state
        }

        is NewsFeedIntent.RefreshComplete -> when (state) {
            is NewsFeedState.Content -> state.copy(contentState = ContentState.Idle)
            is NewsFeedState.Empty -> state.copy(isRefreshing = false)
            is NewsFeedState.Error -> state.copy(isRefreshing = false)
            else -> state
        }

        is NewsFeedIntent.RefreshFailure -> when {
            // Нет интернета и есть кэш — показываем offline баннер
            intent.error is NetworkError.NoInternet && intent.cachedClusters.isNotEmpty() ->
                NewsFeedState.Content(
                    clusters = intent.cachedClusters,
                    country = state.contentOrNull?.country ?: "us",
                    language = state.contentOrNull?.language ?: "en",
                    lastCachedAt = intent.lastCachedAt,
                    contentState = ContentState.Offline(intent.lastCachedAt),
                )
            // Стейт уже Content (кэш показан) — не перекрываем ошибкой,
            // только сбрасываем refreshing, ошибка уйдёт через sideEffect
            state is NewsFeedState.Content ->
                state.copy(contentState = ContentState.Idle)
            // Кэша нет совсем — показываем экран ошибки
            else ->
                NewsFeedState.Error(errorType = intent.error, isRefreshing = false)
        }

        is NewsFeedIntent.ArticleClick -> state
    }
}