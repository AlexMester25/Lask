package dev.alexmester.lask.di

import dev.alexmester.database.di.databaseModule
import dev.alexmester.datastore.di.dataStoreModule
import dev.alexmester.impl.di.articleDetailModule
import dev.alexmester.impl.di.bookmarksModule
import dev.alexmester.impl.di.newsFeedModule
import dev.alexmester.impl.di.profileModule
import dev.alexmester.lask.splash_screen.splashModule
import dev.alexmester.lask.theme_switch.themeSwitchModule
import dev.alexmester.network.di.networkModule

object AppModules {
    val all = listOf(
        appScopeModule,
        splashModule,
        themeSwitchModule,
        featuresNavigationModule,
        // Core
        networkModule,
        databaseModule,
        dataStoreModule,
        // Features
        newsFeedModule,
        articleDetailModule,
        bookmarksModule,
        profileModule,
    )
}