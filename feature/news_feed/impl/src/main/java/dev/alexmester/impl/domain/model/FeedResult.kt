package dev.alexmester.impl.domain.model

import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.news.NewsCluster

sealed interface FeedResult {
    data object Idle : FeedResult
    data object Loading : FeedResult
    data object Success : FeedResult
    data class Error(val error: NetworkError) : FeedResult
}