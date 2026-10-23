package dev.alexmester.impl.di

import dev.alexmester.impl.presentation.mvi.BookmarksViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val bookmarks = module {
    viewModel<BookmarksViewModel>()
}
