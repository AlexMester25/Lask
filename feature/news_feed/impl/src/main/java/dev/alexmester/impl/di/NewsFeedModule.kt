package dev.alexmester.impl.di

import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.impl.data.repository.NewsFeedRepositoryImpl
import dev.alexmester.impl.domain.repository.NewsFeedRepository
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.impl.navigation.NewsFeedImpl
import dev.alexmester.impl.presentation.mvi.NewsFeedViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val newsFeed = module {
    single<NewsFeedImpl>() bind NewsFeedApi::class
    single<NewsFeedRepositoryImpl>() bind NewsFeedRepository::class
    factory<ObserveTrendsUseCase>()
    viewModel<NewsFeedViewModel>()
}