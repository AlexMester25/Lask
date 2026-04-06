package dev.alexmester.api.navigation

import dev.alexmester.navigation.FeatureApi

interface NewsFeedApi : FeatureApi {

    fun feedRoute(): NewsFeedRoute
}