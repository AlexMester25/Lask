package dev.alexmester.impl.presentation.mvi

sealed interface NewsFeedIntent {
    data object Refresh : NewsFeedIntent
    data class ArticleClick(val articleId: Long, val articleUrl: String) : NewsFeedIntent
}
