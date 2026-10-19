package dev.alexmester.impl.di

import dev.alexmester.impl.data.repository.ExploreRepositoryImpl
import dev.alexmester.impl.domain.repository.ExploreRepository
import dev.alexmester.impl.domain.usecase.LoadMoreExploreUseCase
import dev.alexmester.impl.domain.usecase.RefreshExploreUseCase
import dev.alexmester.impl.presentation.mvi.ExploreViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val explore = module {

    single<ExploreRepository> {
        ExploreRepositoryImpl(
            remote = get(),
            feedCache = get(),
            userPreferencesRepository = get(),
            dispatchers = get()
        )
    }

    factory { LoadMoreExploreUseCase(repository = get()) }
    factory { RefreshExploreUseCase(repository = get()) }

    viewModel {
        ExploreViewModel(
            articleRepository = get(),
            exploreRepository = get(),
            refreshExplore = get(),
            loadMore = get(),
        )
    }
}