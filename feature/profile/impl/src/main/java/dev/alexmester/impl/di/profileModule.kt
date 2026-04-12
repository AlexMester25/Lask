package dev.alexmester.impl.di

import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.impl.data.local.ArticleListLocalDataSource
import dev.alexmester.impl.data.repository.ArticleListRepositoryImpl
import dev.alexmester.impl.domain.interactor.ArticleListInteractor
import dev.alexmester.impl.domain.interactor.ProfileInteractor
import dev.alexmester.impl.domain.repository.ArticleListRepository
import dev.alexmester.impl.presentation.article_list.mvi.ArticleListViewModel
import dev.alexmester.impl.presentation.profile.mvi.ProfileViewModel
import dev.alexmester.models.di.DISPATCHER_IO
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val profileModule = module {

    // ── ArticleList ───────────────────────────────────────────────────────────

    single {
        ArticleListLocalDataSource(
            userStateDao = get(),
            ioDispatcher = get(named(DISPATCHER_IO)),
        )
    }

    single<ArticleListRepository> {
        ArticleListRepositoryImpl(local = get())
    }

    factory {
        ArticleListInteractor(repository = get())
    }

    viewModel { (type: ArticleListType) ->
        ArticleListViewModel(
            type = type,
            interactor = get(),
        )
    }

    // ── Profile ───────────────────────────────────────────────────────────────

    factory {
        ProfileInteractor(
            preferencesDataSource = get(),
            articleUserStateDao = get(),
        )
    }

    viewModel {
        ProfileViewModel(profileInteractor = get())
    }
}