package dev.alexmester.impl.domain.model

sealed interface RefreshResult {
    data class Success(val count: Int) : RefreshResult
    data object NoInterests : RefreshResult
    data object EmptySearchResult : RefreshResult
}