package dev.alexmester.newsfeed.impl.presentation.feed

import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsCluster

sealed class NewsFeedIntent {
    data object Refresh : NewsFeedIntent()
    data class ArticleClick(val articleId: Long, val articleUrl: String) : NewsFeedIntent()
}
