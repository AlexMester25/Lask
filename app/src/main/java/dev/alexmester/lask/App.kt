package dev.alexmester.lask

import android.app.Application
import dev.alexmester.database.di.databaseModule
import dev.alexmester.datastore.di.dataStoreModule
import dev.alexmester.network.di.networkModule
import dev.alexmester.posts.di.postsModule
import dev.alexmester.users.di.usersModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level


class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)

            modules(
                networkModule,
                databaseModule,
                dataStoreModule,


                postsModule,
                usersModule
            )
        }
    }
}