package dev.alexmester.api.navigation

import dev.alexmester.navigation.FeatureApi

interface ProfileApi : FeatureApi {
    fun profileRoute(): ProfileRoute
}