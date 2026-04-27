package dev.alexmester.database.di

import androidx.room.Room
import dev.alexmester.database.AppDatabase
import dev.alexmester.database.di.DatabaseConstants.DB_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = AppDatabase::class.java,
            name = DB_NAME,
        )
            .fallbackToDestructiveMigrationFrom()
            .build()
    }

    single { get<AppDatabase>().articleDao() }
    single { get<AppDatabase>().articleUserStateDao()}
    single { get<AppDatabase>().feedCacheDao() }
}

object DatabaseConstants {
    const val DB_NAME = "lask_database"
    const val DB_VERSION = 5
}