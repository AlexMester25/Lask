package dev.alexmester.impl.di

import dev.alexmester.impl.data.repository.InterestsRepositoryImpl
import dev.alexmester.impl.domain.repository.InterestsRepository
import dev.alexmester.impl.domain.usecase.AddInterestUseCase
import dev.alexmester.impl.domain.usecase.RemoveInterestUseCase
import dev.alexmester.impl.presentation.interests.mvi.InterestsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val interests = module {

    single<InterestsRepository> {
        InterestsRepositoryImpl(
            preferencesDataSource = get()
        )
    }

    factory { AddInterestUseCase(repository = get()) }
    factory { RemoveInterestUseCase(repository = get()) }

    viewModel {
        InterestsViewModel(
            observeUserPreferencesUseCase = get(),
            addInterestUseCase = get(),
            removeInterestUseCase = get(),
        )
    }
}