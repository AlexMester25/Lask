package dev.alexmester.impl.di

import dev.alexmester.api.navigation.SearchApi
import dev.alexmester.impl.data.repository.SearchRepositoryImpl
import dev.alexmester.impl.domain.repository.SearchRepository
import dev.alexmester.impl.navigation.SearchImpl
import dev.alexmester.impl.presentation.mvi.SearchViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val searchModule = module {
    single<SearchImpl>() bind SearchApi::class
    single<SearchRepositoryImpl>() bind SearchRepository::class
    viewModel<SearchViewModel>()
}
