package dev.alexmester.newsfeed.impl.presentation.feed

sealed interface NewsFeedIntent {
    data object Refresh : NewsFeedIntent
    data class ArticleClick(val articleId: Long, val articleUrl: String) : NewsFeedIntent
}
