package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.NewsFeedLocalDataSource
import dev.alexmester.impl.data.remote.NewsFeedApiService
import dev.alexmester.impl.data.repository.NewsFeedRepositoryImpl
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.impl.domain.usecase.GetCurrentLocaleTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.impl.domain.usecase.GetCachedAtTrendsUseCase
import dev.alexmester.impl.domain.usecase.ObserveReadArticleIdsTrendsUseCase
import dev.alexmester.impl.domain.usecase.RefreshTrendsUseCase
import dev.alexmester.impl.presentation.mvi.NewsFeedViewModel
import dev.alexmester.models.di.DISPATCHER_IO
import dev.alexmester.network.di.Clients
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val newsFeedModule = module {

    single { NewsFeedApiService(client = get(named(Clients.WORLD_NEWS))) }

    single {
        NewsFeedLocalDataSource(
            db = get(),
            articleDao = get(),
            feedCacheDao = get(),
            userStateDao = get(),
            ioDispatcher = get(named(DISPATCHER_IO)),
        )
    }

    single<NewsFeedRepository> {
        NewsFeedRepositoryImpl(
            remote = get(),
            local = get(),
            preferencesDataSource = get()
        )
    }

    factory { ObserveTrendsUseCase(repository = get(), preferencesDataSource = get()) }
    factory { ObserveReadArticleIdsTrendsUseCase(repository = get()) }
    factory { GetCurrentLocaleTrendsUseCase(repository = get()) }
    factory { GetCachedAtTrendsUseCase(repository = get()) }
    single { RefreshTrendsUseCase(repository = get(), preferencesDataSource = get()) }


    viewModel {
        NewsFeedViewModel(
            observeFeedClustersUseCase = get(),
            refreshFeedUseCase = get(),
            observeReadArticleIdsUseCase = get(),
            getCurrentLocaleUseCase = get(),
            getLastCachedAtUseCase = get(),
        )
    }
}