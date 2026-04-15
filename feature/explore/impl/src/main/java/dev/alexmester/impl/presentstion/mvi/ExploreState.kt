package dev.alexmester.impl.presentstion.mvi

import dev.alexmester.models.news.NewsArticle
import dev.alexmester.ui.uitext.UiText

sealed interface ExploreState {
    data object Loading : ExploreState
    data class Error(val message: UiText, val isRefreshing: Boolean = false) : ExploreState
    data object EmptyInterests : ExploreState
    data class Content(
        val articles: List<NewsArticle>,
        val isRefreshing: Boolean = false,
        val isLoadingMore: Boolean = false,
        val endReached: Boolean = false,
        val lastCachedAt: Long? = null,
        val isOffline: Boolean = false,
    ) : ExploreState
}