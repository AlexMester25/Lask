package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.news.NewsCluster
import dev.alexmester.ui.uitext.UiText

sealed interface NewsFeedScreenState {
    data object Loading : NewsFeedScreenState
    data class Error(val message: UiText) : NewsFeedScreenState
    data class Content(
        val clusters: List<NewsCluster>,
        val country: String = "ru",
        val lastCachedAt: Long? = null,
        val contentState: ContentState = ContentState.Idle,
    ) : NewsFeedScreenState
}

sealed interface ContentState {
    data object Idle : ContentState
    data object Refreshing : ContentState
    data class Offline(val lastCachedAt: Long?) : ContentState
}
