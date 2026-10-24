package dev.alexmester.lask.di

import dev.alexmester.data.di.data
import dev.alexmester.database.di.database
import dev.alexmester.datastore.di.dataStore
import dev.alexmester.impl.di.articleDetailModule
import dev.alexmester.impl.di.bookmarksModule
import dev.alexmester.impl.di.exploreModule
import dev.alexmester.impl.di.interestsModule
import dev.alexmester.impl.di.newsFeedModule
import dev.alexmester.impl.di.profileModule
import dev.alexmester.impl.di.searchModule
import dev.alexmester.impl.di.typedArticleListModule
import dev.alexmester.lask.main.mainModule
import dev.alexmester.network.di.network
import dev.alexmester.platform.di.platform

object AppModules {
    val all = listOf(
        mainModule,
        // Core
        network,
        database,
        dataStore,
        platform,
        data,
        // Features
        newsFeedModule,
        exploreModule,
        articleDetailModule,
        bookmarksModule,
        profileModule,
        searchModule,
        typedArticleListModule,
        interestsModule,
    )
}
