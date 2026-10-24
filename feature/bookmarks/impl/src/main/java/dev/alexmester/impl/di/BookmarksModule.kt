package dev.alexmester.impl.di

import dev.alexmester.api.navigation.BookmarksApi
import dev.alexmester.impl.navigation.BookmarksImpl
import dev.alexmester.impl.presentation.mvi.BookmarksViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val bookmarksModule = module {
    single<BookmarksImpl>() bind BookmarksApi::class
    viewModel<BookmarksViewModel>()
}
