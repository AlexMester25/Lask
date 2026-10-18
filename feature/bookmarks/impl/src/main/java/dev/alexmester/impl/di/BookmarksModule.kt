package dev.alexmester.impl.di

import dev.alexmester.impl.data.local.BookmarksLocalDataSource
import dev.alexmester.impl.data.repository.BookmarksRepositoryImpl
import dev.alexmester.impl.domain.repository.BookmarksRepository
import dev.alexmester.impl.presentation.mvi.BookmarksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bookmarksModule = module {

    single {
        BookmarksLocalDataSource(userStateDao = get())
    }

    single<BookmarksRepository> {
        BookmarksRepositoryImpl(
            local = get(),
            dispatchers = get()
        )
    }

    viewModel {
        BookmarksViewModel(bookmarksRepository = get())
    }
}
