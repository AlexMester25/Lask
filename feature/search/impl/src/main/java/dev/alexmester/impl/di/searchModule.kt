package dev.alexmester.impl.di

import dev.alexmester.impl.data.remote.SearchApiService
import dev.alexmester.impl.data.repository.SearchRepositoryImpl
import dev.alexmester.impl.domain.interactor.SearchInteractor
import dev.alexmester.impl.domain.repository.SearchRepository
import dev.alexmester.impl.presentation.mvi.SearchViewModel
import dev.alexmester.network.di.Clients
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val searchModule = module {
    single { SearchApiService(client = get(named(Clients.WORLD_NEWS))) }
    single<SearchRepository> {
        SearchRepositoryImpl(
            remote = get(),
            articleDao = get(),
            userStateDao = get()
        )
    }
    factory { SearchInteractor(repository = get()) }
    viewModel { SearchViewModel(interactor = get()) }
}