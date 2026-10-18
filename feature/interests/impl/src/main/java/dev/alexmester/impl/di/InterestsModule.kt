package dev.alexmester.impl.di

import dev.alexmester.impl.data.repository.InterestsRepositoryImpl
import dev.alexmester.impl.domain.repository.InterestsRepository
import dev.alexmester.impl.presentation.mvi.InterestsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val interests = module {

    single<InterestsRepository> {
        InterestsRepositoryImpl(
            preferencesDataSource = get()
        )
    }

    viewModel {
        InterestsViewModel(
            userPreferencesRepository = get(),
            interestsRepository = get(),
        )
    }
}