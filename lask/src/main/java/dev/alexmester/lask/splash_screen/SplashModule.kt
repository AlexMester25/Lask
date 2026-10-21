package dev.alexmester.lask.splash_screen

import dev.alexmester.platform.locale.DeviceLocaleProvider
import dev.alexmester.platform.locale.LocaleChangeObserver
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val splash = module {
    viewModel {
        SplashViewModel(
            userPreferencesRepository = get(),
            deviceLocaleProvider = get(),
        )
    }
    single { DeviceLocaleProvider(androidContext()) }
    single {
        LocaleChangeObserver(
            context = androidContext(),
            deviceLocaleProvider = get(),
            userPreferencesRepository = get(),
            scope = get<CoroutineScope>(),
        )
    }
}