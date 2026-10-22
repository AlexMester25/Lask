package dev.alexmester.platform.di

import dev.alexmester.platform.dispatchers.DefaultDispatcherProvider
import dev.alexmester.platform.dispatchers.DispatcherProvider
import dev.alexmester.platform.locale.DeviceLocaleProvider
import dev.alexmester.platform.locale.DeviceLocaleSynchronizer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import java.time.Clock

val platform = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<Clock> { Clock.systemDefaultZone() }
    single { DeviceLocaleProvider(context = androidContext()) }
    single<DeviceLocaleSynchronizer>()
}