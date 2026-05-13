package dev.alexmester.impl.domain.model

sealed interface RefreshResult {
    data class Updated(val count: Int) : RefreshResult
    data object NoInterests : RefreshResult
    data object EmptySearchResult : RefreshResult
    data object CacheFresh : RefreshResult
}