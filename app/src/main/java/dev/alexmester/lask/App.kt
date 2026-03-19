package dev.alexmester.lask

import android.app.Application
import dev.alexmester.network.di.networkModule
import dev.alexmester.posts.di.postsModule
import dev.alexmester.users.di.usersModule
import dev.alexmester.database.di.databaseModule
import dev.alexmester.network.di.newsApiKey
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)

            modules(
                module { single(newsApiKey) { BuildConfig.NEWS_API_KEY } },
                networkModule,
                databaseModule,

                // Feature modules
                postsModule,
                usersModule
            )
        }
    }
}