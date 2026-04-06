package dev.alexmester.api.navigation

import dev.alexmester.navigation.FeatureApi

interface BookmarksApi : FeatureApi {
    fun bookmarkRoute(): BookmarkRoute
}