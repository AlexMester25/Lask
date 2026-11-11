package dev.alexmester.impl.domain.model

import dev.alexmester.models.error.AppError

sealed interface FeedLoadState {
    data object Idle : FeedLoadState
    data object Loading : FeedLoadState
    data object Success : FeedLoadState
    data class Error(val error: AppError) : FeedLoadState
}
