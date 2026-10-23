package dev.alexmester.lask.di

import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.BookmarksApi
import dev.alexmester.api.navigation.ExploreApi
import dev.alexmester.api.navigation.InterestsApi
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.api.navigation.ProfileApi
import dev.alexmester.api.navigation.SearchApi
import dev.alexmester.api.navigation.TypedArticleListApi
import dev.alexmester.impl.navigation.ArticleDetailImpl
import dev.alexmester.impl.navigation.BookmarksImpl
import dev.alexmester.impl.navigation.ExploreImpl
import dev.alexmester.impl.navigation.InterestsImpl
import dev.alexmester.impl.navigation.NewsFeedImpl
import dev.alexmester.impl.navigation.ProfileImpl
import dev.alexmester.impl.navigation.SearchImpl
import dev.alexmester.impl.navigation.TypedArticleListImpl
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.*

val featuresNavigation = module {
    single<SearchImpl>() bind SearchApi::class
    single<NewsFeedImpl>() bind NewsFeedApi::class
    single<ExploreImpl>() bind ExploreApi::class
    single<ArticleDetailImpl>() bind ArticleDetailApi::class
    single<BookmarksImpl>() bind BookmarksApi::class
    single<ProfileImpl>() bind ProfileApi::class
    single<TypedArticleListImpl>() bind TypedArticleListApi::class
    single<InterestsImpl>() bind InterestsApi::class
}