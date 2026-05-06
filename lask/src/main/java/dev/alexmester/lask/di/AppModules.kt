package dev.alexmester.lask.di

import dev.alexmester.database.di.database
import dev.alexmester.datastore.di.dataStore
import dev.alexmester.impl.di.articleDetailModule
import dev.alexmester.impl.di.bookmarksModule
import dev.alexmester.impl.di.explore
import dev.alexmester.impl.di.newsFeed
import dev.alexmester.impl.di.profile
import dev.alexmester.impl.di.search
import dev.alexmester.lask.splash_screen.splash
import dev.alexmester.lask.theme_switch.themeSwitch
import dev.alexmester.network.di.network
import dev.alexmester.platform.dispatchers.platform

object AppModules {
    val all = listOf(
        splash,
        themeSwitch,
        featuresNavigation,
        // Core
        network,
        database,
        dataStore,
        platform,
        // Features
        newsFeed,
        explore,
        articleDetailModule,
        bookmarksModule,
        profile,
        search
    )
}