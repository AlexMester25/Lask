package dev.alexmester.lask.di

import dev.alexmester.models.di.DISPATCHER_DEFAULT
import dev.alexmester.models.di.DISPATCHER_IO
import dev.alexmester.models.di.DISPATCHER_MAIN
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appScopeModule = module {
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    single<CoroutineDispatcher>(named(DISPATCHER_IO)) { Dispatchers.IO }
    single<CoroutineDispatcher>(named(DISPATCHER_DEFAULT)) { Dispatchers.Default }
    single<CoroutineDispatcher>(named(DISPATCHER_MAIN)) { Dispatchers.Main }
}