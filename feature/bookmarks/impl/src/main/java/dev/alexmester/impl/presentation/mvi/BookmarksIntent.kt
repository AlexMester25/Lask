package dev.alexmester.impl.presentation.mvi

sealed class BookmarksIntent {
    data object ToggleEditMode : BookmarksIntent()
    data class TogglePendingRemoval(val articleId: Long) : BookmarksIntent()
    data object ConfirmDeletion : BookmarksIntent()
    data object CancelDeletion : BookmarksIntent()
    data class ArticleClick(val articleId: Long, val articleUrl: String) : BookmarksIntent()
}