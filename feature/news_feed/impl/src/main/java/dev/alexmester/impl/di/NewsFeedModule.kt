package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.NewsFeedLocalDataSource
import dev.alexmester.impl.data.remote.NewsFeedApiService
import dev.alexmester.impl.data.repository.NewsFeedRepositoryImpl
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.impl.presentation.mvi.NewsFeedViewModel
import dev.alexmester.network.di.Clients
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val newsFeed = module {

    single { NewsFeedApiService(client = get(named(Clients.WORLD_NEWS))) }

    single {
        NewsFeedLocalDataSource(
            transactionRunner = get(),
            articleDao = get(),
            feedCacheDao = get(),
        )
    }

    single<NewsFeedRepository> {
        NewsFeedRepositoryImpl(
            remote = get(),
            local = get(),
            preferencesDataSource = get(),
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