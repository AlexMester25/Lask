package dev.alexmester.impl.domain.model

sealed interface RefreshExploreResult {
    data class Updated(val count: Int) : RefreshExploreResult
    data object NoInterests : RefreshExploreResult
    data object EmptySearchResult : RefreshExploreResult
    data object CacheFresh : RefreshExploreResult
}