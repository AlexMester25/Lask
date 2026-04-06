package dev.alexmester.impl.presentation.mvi

sealed class BookmarksSideEffect {
    data class NavigateToArticle(
        val articleId: Long,
        val articleUrl: String,
    ) : BookmarksSideEffect()
}