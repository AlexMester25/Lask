package dev.alexmester.impl.di

import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerViewModel
import dev.alexmester.impl.presentation.profile.mvi.ProfileViewModel
import dev.alexmester.impl.presentation.system.mvi.SystemViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profile = module {

    viewModel {
        ProfileViewModel(
            userPreferencesRepository = get(),
            articleRepository = get()
        )
    }
    viewModel { SystemViewModel(userPreferencesRepository = get()) }
    viewModel { (type: LocalePickerType) ->
        LocalePickerViewModel(type = type, userPreferencesRepository = get())
    }
}