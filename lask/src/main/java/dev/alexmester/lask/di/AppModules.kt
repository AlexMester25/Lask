package dev.alexmester.lask.di

import dev.alexmester.data.di.data
import dev.alexmester.database.di.database
import dev.alexmester.datastore.di.dataStore
import dev.alexmester.impl.di.articleDetail
import dev.alexmester.impl.di.bookmarks
import dev.alexmester.impl.di.explore
import dev.alexmester.impl.di.interests
import dev.alexmester.impl.di.newsFeed
import dev.alexmester.impl.di.profile
import dev.alexmester.impl.di.search
import dev.alexmester.impl.di.typedArticleList
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
        newsFeed,
        explore,
        articleDetail,
        bookmarks,
        profile,
        search,
        typedArticleList,
        interests,
    )
}