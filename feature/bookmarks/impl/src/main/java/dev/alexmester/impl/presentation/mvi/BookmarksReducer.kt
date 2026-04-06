package dev.alexmester.impl.presentation.mvi

import dev.alexmester.models.news.NewsArticle

object BookmarksReducer {

    fun reduce(state: BookmarksState, intent: BookmarksIntent): BookmarksState {
        val content = state.contentOrNull ?: return state

        return when (intent) {
            is BookmarksIntent.ToggleEditMode -> content.copy(
                isEditMode = !content.isEditMode,
                pendingRemovalIds = if (content.isEditMode) emptySet() else content.pendingRemovalIds,
            )

            is BookmarksIntent.TogglePendingRemoval -> {
                val newSet = content.pendingRemovalIds.toMutableSet()
                if (intent.articleId in newSet) newSet.remove(intent.articleId)
                else newSet.add(intent.articleId)
                content.copy(pendingRemovalIds = newSet)
            }

            else -> state
        }
    }
    fun onCloseEditMode(state: BookmarksState): BookmarksState {
        return state.contentOrNull?.copy(
            isEditMode = false,
            pendingRemovalIds = emptySet(),
        ) ?: state
    }

    fun onArticlesUpdated(
        state: BookmarksState,
        articles: List<NewsArticle>,
    ): BookmarksState = when {
        articles.isEmpty() -> BookmarksState.Empty
        state is BookmarksState.Content -> state.copy(articles = articles)
        else -> BookmarksState.Content(articles = articles)
    }
}