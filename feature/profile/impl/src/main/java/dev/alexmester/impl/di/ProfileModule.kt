package dev.alexmester.impl.di

import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.impl.data.local.ProfileLocalDataSource
import dev.alexmester.impl.data.repository.ProfileRepositoryImpl
import dev.alexmester.impl.domain.repository.ProfileRepository
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerViewModel
import dev.alexmester.impl.presentation.profile.mvi.ProfileViewModel
import dev.alexmester.impl.presentation.system.mvi.SystemViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profile = module {

    single {
        ProfileLocalDataSource(
            articleRepository = get(),
            preferencesDataSource = get(),
        )
    }

    single<ProfileRepository> {
        ProfileRepositoryImpl(local = get())
    }

    viewModel {
        ProfileViewModel(
            profileRepository = get(),
        )
    }

    viewModel {
        SystemViewModel(
            userPreferencesRepository = get(),
            profileRepository = get(),
        )
    }

    viewModel { (type: LocalePickerType) ->
        LocalePickerViewModel(
            type = type,
            userPreferencesRepository = get(),
            profileRepository = get(),
        )
    }
}