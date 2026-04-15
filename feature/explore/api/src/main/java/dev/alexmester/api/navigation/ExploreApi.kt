package dev.alexmester.api.navigation

import dev.alexmester.navigation.FeatureApi

interface ExploreApi : FeatureApi {

    fun exploreRoute(): ExploreRoute
}