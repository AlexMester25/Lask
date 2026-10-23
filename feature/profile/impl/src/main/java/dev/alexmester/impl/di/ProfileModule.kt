package dev.alexmester.impl.di

import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerViewModel
import dev.alexmester.impl.presentation.profile.mvi.ProfileViewModel
import dev.alexmester.impl.presentation.system.mvi.SystemViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val profile = module {
    viewModel<ProfileViewModel>()
    viewModel<SystemViewModel>()
    viewModel { (type: LocalePickerType) ->
        LocalePickerViewModel(type = type, userPreferencesRepository = get())
    }
}