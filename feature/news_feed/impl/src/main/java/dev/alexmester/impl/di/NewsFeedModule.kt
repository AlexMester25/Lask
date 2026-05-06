package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.NewsFeedLocalDataSource
import dev.alexmester.impl.data.remote.NewsFeedApiService
import dev.alexmester.impl.data.repository.NewsFeedRepositoryImpl
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.impl.domain.usecase.GetCachedAtTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.impl.domain.usecase.RefreshTrendsUseCase
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
            userStateDao = get(),
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

    factory { ObserveTrendsUseCase(repository = get()) }
    factory { ObserveReadArticleIdsTrendsUseCase(repository = get()) }
    factory { GetCachedAtTrendsUseCase(repository = get()) }
    factory { RefreshTrendsUseCase(repository = get()) }

    viewModel {
        NewsFeedViewModel(
            observeTrendsUseCase = get(),
            refreshTrendsUseCase = get(),
            observeReadArticleIdsUseCase = get(),
            getCachedAtTrendsUseCase = get(),
        )
    }
}