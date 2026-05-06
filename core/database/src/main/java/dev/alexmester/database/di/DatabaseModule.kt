package dev.alexmester.database.di

import androidx.room.Room
import dev.alexmester.database.AppDatabase
import dev.alexmester.database.DatabaseConstants.DB_NAME
import dev.alexmester.database.dao.TransactionRunner
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val database = module {

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
    single<TransactionRunner> { get<AppDatabase>().transactionRunnerDao() }
}

