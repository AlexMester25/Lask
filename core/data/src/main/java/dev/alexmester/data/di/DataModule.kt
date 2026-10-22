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
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

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
    single<NewsRemoteDataSourceImpl>() bind NewsRemoteDataSource::class
    single<TranslateRemoteDataSourceImpl>() bind TranslateRemoteDataSource::class
    single<FeedCacheImpl>() bind FeedCache::class
    single<UserPreferencesRepositoryImpl>() bind UserPreferencesRepository::class
    single<ArticleRepositoryImpl>() bind ArticleRepository::class
}