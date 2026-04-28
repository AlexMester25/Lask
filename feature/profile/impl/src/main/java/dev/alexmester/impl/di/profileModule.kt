package dev.alexmester.impl.di

import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.impl.data.local.ProfileLocalDataSource
import dev.alexmester.impl.data.repository.ProfileRepositoryImpl
import dev.alexmester.impl.domain.repository.ProfileRepository
import dev.alexmester.impl.domain.usecase.AddInterestUseCase
import dev.alexmester.impl.domain.usecase.ApplyEditChangesUseCase
import dev.alexmester.impl.domain.usecase.ObserveClappedArticlesUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticlesUseCase
import dev.alexmester.impl.domain.usecase.ObserveProfileUseCase
import dev.alexmester.impl.domain.usecase.ObserveUserPreferencesUseCase
import dev.alexmester.impl.domain.usecase.RemoveInterestUseCase
import dev.alexmester.impl.domain.usecase.UpdateAutoTranslateLanguageUseCase
import dev.alexmester.impl.domain.usecase.UpdateLocaleManuallyUseCase
import dev.alexmester.impl.domain.usecase.UpdateStreakUseCase
import dev.alexmester.impl.domain.usecase.UpdateThemeUseCase
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListViewModel
import dev.alexmester.impl.presentation.interests.mvi.InterestsViewModel
import dev.alexmester.impl.presentation.locale_picker.mvi.LocalePickerViewModel
import dev.alexmester.impl.presentation.profile.mvi.ProfileViewModel
import dev.alexmester.impl.presentation.system.mvi.SystemViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val profileModule = module {

    single {
        ProfileLocalDataSource(
            userStateDao = get(),
            preferencesDataSource = get(),
        )
    }

    single<ProfileRepository> {
        ProfileRepositoryImpl(local = get())
    }

    factory { ObserveReadArticlesUseCase(repository = get()) }
    factory { ObserveClappedArticlesUseCase(repository = get()) }
    factory { ObserveProfileUseCase(repository = get()) }
    factory { ObserveUserPreferencesUseCase(repository = get()) }
    factory { ApplyEditChangesUseCase(repository = get()) }
    factory { UpdateStreakUseCase(repository = get()) }
    factory { UpdateThemeUseCase(repository = get()) }
    factory { AddInterestUseCase(repository = get()) }
    factory { RemoveInterestUseCase(repository = get()) }
    factory { UpdateLocaleManuallyUseCase(repository = get()) }
    factory { UpdateAutoTranslateLanguageUseCase(repository = get()) }

    viewModel {
        ProfileViewModel(
            observeProfileUseCase = get(),
            updateStreakUseCase = get(),
            applyEditChangesUseCase = get(),
        )
    }

    viewModel { (type: ArticleListType) ->
        ArticleListViewModel(
            type = type,
            observeReadArticlesUseCase = get(),
            observeClappedArticlesUseCase = get(),
        )
    }

    viewModel {
        SystemViewModel(
            observeUserPreferencesUseCase = get(),
            updateThemeUseCase = get(),
        )
    }

    viewModel {
        InterestsViewModel(
            observeUserPreferencesUseCase = get(),
            addInterestUseCase = get(),
            removeInterestUseCase = get(),
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