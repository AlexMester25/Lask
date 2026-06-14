package dev.alexmester.impl.di

import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.domain.usecase.ObserveUserPreferencesUseCase
import dev.alexmester.impl.data.local.ProfileLocalDataSource
import dev.alexmester.impl.data.repository.ProfileRepositoryImpl
import dev.alexmester.impl.domain.repository.ProfileRepository
import dev.alexmester.impl.domain.usecase.ApplyEditChangesUseCase
import dev.alexmester.impl.domain.usecase.ObserveProfileUseCase
import dev.alexmester.impl.domain.usecase.UpdateAutoTranslateLanguageUseCase
import dev.alexmester.impl.domain.usecase.UpdateLocaleManuallyUseCase
import dev.alexmester.impl.domain.usecase.UpdateStreakUseCase
import dev.alexmester.impl.domain.usecase.UpdateThemeUseCase
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerViewModel
import dev.alexmester.impl.presentation.profile.mvi.ProfileViewModel
import dev.alexmester.impl.presentation.system.mvi.SystemViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profile = module {

    single {
        ProfileLocalDataSource(
            userStateDao = get(),
            preferencesDataSource = get(),
        )
    }

    single<ProfileRepository> {
        ProfileRepositoryImpl(local = get())
    }

    factory { ObserveProfileUseCase(repository = get()) }
    factory { ApplyEditChangesUseCase(repository = get()) }
    factory { UpdateStreakUseCase(repository = get()) }
    factory { UpdateThemeUseCase(repository = get()) }
    factory { UpdateLocaleManuallyUseCase(repository = get()) }
    factory { UpdateAutoTranslateLanguageUseCase(repository = get()) }

    viewModel {
        ProfileViewModel(
            observeProfileUseCase = get(),
            updateStreakUseCase = get(),
            applyEditChangesUseCase = get(),
        )
    }

    viewModel {
        SystemViewModel(
            observeUserPreferencesUseCase = get(),
            updateThemeUseCase = get(),
        )
    }

    viewModel { (type: LocalePickerType) ->
        LocalePickerViewModel(
            type = type,
            observeUserPreferencesUseCase = get(),
            updateLocaleManuallyUseCase = get(),
            updateAutoTranslateLanguageUseCase = get(),
        )
    }
}