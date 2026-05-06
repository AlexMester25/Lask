package dev.alexmester.network.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object BaseClient {

    fun createBaseClient() = HttpClient(Android) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            })
        }

        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.HEADERS
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 30_000
        }

        install(HttpRequestRetry) {
            retryIf { _, response ->
                response.status.value in 500..599
            }
            exponentialDelay(
                base = 2.0,
                maxDelayMs = 10_000,
            )
            maxRetries = 3
        }
    }

    const val BASE_URL_WORLD_NEWS = "https://api.worldnewsapi.com/"
    const val BASE_URL_TRANSLATE = "https://api.translateplus.io/"

}