package dev.alexmester.impl.di

import dev.alexmester.impl.presentation.mvi.BookmarksViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bookmarksModule = module {

    viewModel {
        BookmarksViewModel(articleRepository  = get())
    }
}
