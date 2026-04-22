package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.ExploreLocalDataSource
import dev.alexmester.impl.data.remote.ExploreApiService
import dev.alexmester.impl.data.repository.ExploreRepositoryImpl
import dev.alexmester.impl.domain.interactor.ExploreInteractor
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.impl.presentstion.mvi.ExploreViewModel
import dev.alexmester.models.di.DISPATCHER_IO
import dev.alexmester.network.di.Clients
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val exploreModule = module {
    single { ExploreApiService(client = get(named(Clients.WORLD_NEWS))) }

    single {
        ExploreLocalDataSource(
            db = get(),
            articleDao = get(),
            feedCacheDao = get(),
            userStateDao = get(),
            ioDispatcher = get(named(DISPATCHER_IO)),
        )
    }

    single<ExploreRepository> {
        ExploreRepositoryImpl(
            remote = get(),
            local = get(),
        )
    }

    factory {
        ExploreInteractor(
            repository = get(),
            preferencesDataSource = get(),
        )
    }

    viewModel { ExploreViewModel(interactor = get()) }
}