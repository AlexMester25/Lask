package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.news.NewsCluster
import dev.alexmester.ui.uitext.UiText

sealed interface NewsFeedScreenState {
    data object Loading : NewsFeedScreenState
    data class Error(
        val message: UiText,
        val isRefreshing: Boolean = false,
        ) : NewsFeedScreenState
    data class Content(
        val clusters: List<NewsCluster>,
        val country: String = "en",
        val lastCachedAt: Long? = null,
        val contentState: ContentState = ContentState.Idle,
    ) : NewsFeedScreenState
    data class Empty(
        val country: String = "en",
        val language: String = "us",
        val isRefreshing: Boolean = false,
    ) : NewsFeedScreenState
}

sealed interface ContentState {
    data object Idle : ContentState
    data object Refreshing : ContentState
    data class Offline(val lastCachedAt: Long?) : ContentState
}


val NewsFeedScreenState.contentOrNull: NewsFeedScreenState.Content?
    get() = this as? NewsFeedScreenState.Content

val NewsFeedScreenState.isLoading: Boolean
    get() = this is NewsFeedScreenState.Loading

val NewsFeedScreenState.isError: Boolean
    get() = this is NewsFeedScreenState.Error

val NewsFeedScreenState.isRefreshing: Boolean
    get() = when (this) {
        is NewsFeedScreenState.Content -> this.contentState is ContentState.Refreshing
        is NewsFeedScreenState.Empty -> this.isRefreshing
        is NewsFeedScreenState.Error -> this.isRefreshing
        else -> false
    }

val NewsFeedScreenState.isOffline: Boolean
    get() = this is NewsFeedScreenState.Content && this.contentState is ContentState.Offline