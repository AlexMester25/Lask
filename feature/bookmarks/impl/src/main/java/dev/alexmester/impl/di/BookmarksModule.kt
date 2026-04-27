package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.BookmarksLocalDataSource
import dev.alexmester.impl.data.repository.BookmarksRepositoryImpl
import dev.alexmester.impl.domain.usecase.ObserveBookmarksUseCase
import dev.alexmester.impl.domain.usecase.RemoveBookmarksUseCase
import dev.alexmester.impl.domain.repository.BookmarksRepository
import dev.alexmester.impl.presentation.mvi.BookmarksViewModel
import dev.alexmester.models.di.DISPATCHER_IO
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val bookmarksModule = module {

    single {
        BookmarksLocalDataSource(
            userStateDao = get(),
            ioDispatcher = get(named(DISPATCHER_IO)),
        )
    }

    single<BookmarksRepository> {
        BookmarksRepositoryImpl(local = get())
    }

    factory { ObserveBookmarksUseCase(repository = get()) }
    factory { RemoveBookmarksUseCase(repository = get()) }

    viewModel {
        BookmarksViewModel(
            observeBookmarksUseCase = get(),
            removeBookmarksUseCase = get()
        )
    }
}
