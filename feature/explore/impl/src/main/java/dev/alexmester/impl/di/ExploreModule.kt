package dev.alexmester.impl.di

import dev.alexmester.api.navigation.ExploreApi
import dev.alexmester.impl.data.repository.ExploreRepositoryImpl
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.impl.domain.usecase.LoadMoreExploreUseCase
import dev.alexmester.impl.domain.usecase.RefreshExploreUseCase
import dev.alexmester.impl.navigation.ExploreImpl
import dev.alexmester.impl.presentation.mvi.ExploreViewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

val explore = module {
    single<ExploreImpl>() bind ExploreApi::class
    single<ExploreRepositoryImpl>() bind ExploreRepository::class
    factory<LoadMoreExploreUseCase>()
    factory<RefreshExploreUseCase>()
    viewModel<ExploreViewModel>()
}