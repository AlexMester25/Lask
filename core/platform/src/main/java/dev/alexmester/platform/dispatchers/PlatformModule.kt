package dev.alexmester.platform.dispatchers

import org.koin.dsl.module
import java.time.Clock

val platform = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<Clock> { Clock.systemDefaultZone() }
}