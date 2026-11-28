package dev.alexmester.api.navigation

import dev.alexmester.navigation.FeatureApi

interface SettingsApi : FeatureApi {
    fun settingsRoute(): SettingsRoute
    fun localePickerRoute(type: LocalePickerType): LocalePickerRoute
}
