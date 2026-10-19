package dev.alexmester.impl.di

import dev.alexmester.impl.data.repository.NewsFeedRepositoryImpl
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.impl.presentation.mvi.NewsFeedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val newsFeed = module {

    single<NewsFeedRepository> {
        NewsFeedRepositoryImpl(
            remote = get(),
            feedCache = get(),
            userPreferencesRepository = get(),
            dispatchers = get(),
        )
    }

    factory {
        ObserveTrendsUseCase(
            newsFeedRepository = get(),
            userPreferencesRepository = get()
        )
    }

    viewModel {
        NewsFeedViewModel(
            userPreferencesRepository = get(),
            articleRepository = get(),
            newsFeedRepository = get(),
            observeTrendsUseCase = get(),
        )
    }
}