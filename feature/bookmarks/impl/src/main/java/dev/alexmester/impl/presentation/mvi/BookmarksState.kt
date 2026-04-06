package dev.alexmester.impl.presentation.mvi

import dev.alexmester.models.news.NewsArticle

sealed interface BookmarksState {
    data object Loading : BookmarksState

    data object Empty : BookmarksState

    data class Content(
        val articles: List<NewsArticle>,
        val isEditMode: Boolean = false,
        val pendingRemovalIds: Set<Long> = emptySet(),
    ) : BookmarksState
}

val BookmarksState.contentOrNull: BookmarksState.Content?
    get() = this as? BookmarksState.Content

val BookmarksState.isContent: Boolean
    get() = this is BookmarksState.Content