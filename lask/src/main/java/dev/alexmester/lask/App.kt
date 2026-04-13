package dev.alexmester.lask

import android.app.Application
import android.util.Log
import coil3.EventListener
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import dev.alexmester.api.navigation.ArticleDetailApi
import dev.alexmester.api.navigation.NewsFeedApi
import dev.alexmester.database.di.databaseModule
import dev.alexmester.datastore.di.dataStoreModule
import dev.alexmester.datastore.util.DeviceLocaleProvider
import dev.alexmester.datastore.util.LocaleChangeObserver
import dev.alexmester.impl.di.articleDetailModule
import dev.alexmester.impl.di.newsFeedModule
import dev.alexmester.impl.navigation.ArticleDetailImpl
import dev.alexmester.impl.navigation.NewsFeedImpl
import dev.alexmester.lask.di.AppModules
import dev.alexmester.lask.welcome_screen.SplashViewModel
import dev.alexmester.models.di.DISPATCHER_DEFAULT
import dev.alexmester.models.di.DISPATCHER_IO
import dev.alexmester.models.di.DISPATCHER_MAIN
import dev.alexmester.network.di.networkModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okio.Path.Companion.toOkioPath
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module



class App : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(AppModules.all)
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, percent = 0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache").toOkioPath())
                    .maxSizeBytes(50L * 1024 * 1024)
                    .build()
            }
            .build()
}