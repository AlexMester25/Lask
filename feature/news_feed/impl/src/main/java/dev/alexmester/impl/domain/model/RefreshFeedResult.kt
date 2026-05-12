package dev.alexmester.impl.domain.model

sealed interface RefreshFeedResult {

    data class Updated(
        val articleCount: Int
    ) : RefreshFeedResult

    data object CacheFresh : RefreshFeedResult

    data object IncompatibleLocale : RefreshFeedResult
}