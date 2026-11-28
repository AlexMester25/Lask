package dev.alexmester.impl.di

import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.api.navigation.SettingsApi
import dev.alexmester.impl.navigation.SettingsImpl
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerViewModel
import dev.alexmester.impl.presentation.settings.mvi.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val settingsModule = module {
    single<SettingsImpl>() bind SettingsApi::class
    viewModel<SettingsViewModel>()
    viewModel { (type: LocalePickerType) ->
        LocalePickerViewModel(type = type, userPreferencesRepository = get())
    }
}
