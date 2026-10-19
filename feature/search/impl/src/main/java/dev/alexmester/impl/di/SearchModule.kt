package dev.alexmester.impl.di

import dev.alexmester.impl.data.repository.SearchRepositoryImpl
import dev.alexmester.impl.domain.repository.SearchRepository
import dev.alexmester.impl.presentation.mvi.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val search = module {

    single<SearchRepository> {
        SearchRepositoryImpl(
            remote = get(),
            articleRepository = get(),
        )
    }

    viewModel {
        SearchViewModel(
            searchRepository = get(),
            articleRepository = get()
        )
    }
}