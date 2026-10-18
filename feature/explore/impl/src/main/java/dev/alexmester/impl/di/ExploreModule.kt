package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.ExploreLocalDataSource
import dev.alexmester.impl.data.remote.ExploreApiService
import dev.alexmester.impl.data.repository.ExploreRepositoryImpl
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.impl.domain.usecase.LoadMoreExploreUseCase
import dev.alexmester.impl.domain.usecase.RefreshExploreUseCase
import dev.alexmester.impl.presentation.mvi.ExploreViewModel
import dev.alexmester.network.di.Clients
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val explore = module {
    single { ExploreApiService(client = get(named(Clients.WORLD_NEWS))) }

    single {
        ExploreLocalDataSource(
            transactionRunner = get(),
            articleDao = get(),
            feedCacheDao = get(),
            preferencesDataSource = get()
        )
    }

    single<ExploreRepository> {
        ExploreRepositoryImpl(
            remote = get(),
            local = get(),
            dispatchers = get()
        )
    }

    factory { LoadMoreExploreUseCase(repository = get()) }
    factory { RefreshExploreUseCase(repository = get()) }

    viewModel {
        ExploreViewModel(
            articleRepository = get(),
            exploreRepository = get(),
            refreshExplore = get(),
            loadMore = get(),
        )
    }
}