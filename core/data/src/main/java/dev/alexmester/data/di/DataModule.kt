package dev.alexmester.data.di

import dev.alexmester.data.cache.FeedCacheImpl
import dev.alexmester.data.remote.api.TranslateApiService
import dev.alexmester.data.remote.source.NewsRemoteDataSourceImpl
import dev.alexmester.data.remote.api.WorldNewsApiService
import dev.alexmester.data.remote.source.TranslateRemoteDataSourceImpl
import dev.alexmester.data.repository.ArticleRepositoryImpl
import dev.alexmester.data.repository.UserPreferencesRepositoryImpl
import dev.alexmester.domain.cache.FeedCache
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.domain.source.NewsRemoteDataSource
import dev.alexmester.domain.source.TranslateRemoteDataSource
import dev.alexmester.network.di.Clients
import org.koin.core.qualifier.named
import org.koin.dsl.module

val data = module {

    single {
        WorldNewsApiService(
            client = get(named(Clients.WORLD_NEWS)),
            clock = get()
        )
    }
    single {
        TranslateApiService(
            client = get(named(Clients.TRANSLATE)),
        )
    }
    single<NewsRemoteDataSource> { NewsRemoteDataSourceImpl(api = get()) }
    single<TranslateRemoteDataSource> { TranslateRemoteDataSourceImpl(api = get()) }

    single<FeedCache> {
        FeedCacheImpl(
            transactionRunner = get(),
            articleDao = get(),
            feedCacheDao = get(),
            clock = get()
        )
    }

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(
            userPreferencesDataSource = get(),
            clock = get()
        )
    }

    single<ArticleRepository> {
        ArticleRepositoryImpl(
            userStateDao = get(),
            articleDao = get(),
            transactionRunner = get(),
            dispatchers = get(),
            clock = get(),
        )
    }
}