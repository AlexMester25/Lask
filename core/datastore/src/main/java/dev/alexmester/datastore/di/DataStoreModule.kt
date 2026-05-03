package dev.alexmester.datastore.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dev.alexmester.datastore.UserPreferencesDataSourceImpl
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.datastore.di.DataStoreConstants.DATASTORE_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

val dataStore = module {

    single<UserPreferencesDataSource>{
        UserPreferencesDataSourceImpl(
            dataStore = androidContext().dataStore,
        )
    }
}

object DataStoreConstants {
    const val DATASTORE_NAME = "user_preferences"
}