package dev.alexmester.api.navigation

import dev.alexmester.navigation.FeatureApi

interface InterestsApi: FeatureApi {
    fun interestsRoute(): InterestsRoute
}