package dev.alexmester.impl.presentation.locale_picker.mvi

sealed class LocalePickerSideEffect {
    data object NavigateBack : LocalePickerSideEffect()
}