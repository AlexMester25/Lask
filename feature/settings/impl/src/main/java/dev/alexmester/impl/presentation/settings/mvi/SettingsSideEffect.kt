package dev.alexmester.impl.presentation.settings.mvi

import dev.alexmester.api.navigation.LocalePickerType

sealed class SettingsSideEffect {
    data object NavigateBack : SettingsSideEffect()
    data class NavigateToLocalePicker(val type: LocalePickerType) : SettingsSideEffect()
    data object NavigateToAutoTranslatePicker : SettingsSideEffect()
}
