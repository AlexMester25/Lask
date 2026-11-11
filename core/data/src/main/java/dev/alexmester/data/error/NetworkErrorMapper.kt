package dev.alexmester.data.error

import dev.alexmester.models.error.NetworkError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpHeaders
import io.ktor.serialization.ContentConvertException
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Переводит исключения сетевого слоя в доменные [NetworkError].
 *
 * Единый маппер для всех API: отличия конкретного сервиса задаются через [statusOverrides],
 * чтобы не заводить отдельный класс-наследник на каждый API.
 */
class NetworkErrorMapper(
    private val statusOverrides: Map<Int, () -> NetworkError> = emptyMap(),
) {

    fun map(throwable: Throwable): NetworkError {
        if (throwable is CancellationException) throw throwable
        return when (throwable) {
            is NetworkError -> throwable
            is HttpRequestTimeoutException,
            is SocketTimeoutException -> NetworkError.Timeout()
            // TCP connect timeout обычно означает недоступность сети, а не медленный сервер.
            is ConnectTimeoutException -> NetworkError.NoInternet()
            is UnknownHostException,
            is UnresolvedAddressException,
            is IOException -> NetworkError.NoInternet()
            // Ktor оборачивает ошибки kotlinx.serialization в ContentConvertException,
            // поэтому исходный SerializationException сам по себе не встретится.
            is ContentConvertException,
            is SerializationException -> NetworkError.ParseError(cause = throwable)
            is ResponseException -> mapHttpError(throwable)
            else -> NetworkError.Unknown(cause = throwable, message = throwable.message)
        }
    }

    private fun mapHttpError(throwable: ResponseException): NetworkError = mapHttpStatus(
        status = throwable.response.status.value,
        description = throwable.response.status.description,
        retryAfter = throwable.response.headers[HttpHeaders.RetryAfter],
    )

    internal fun mapHttpStatus(
        status: Int,
        description: String? = null,
        retryAfter: String? = null,
    ): NetworkError = statusOverrides[status]?.invoke() ?: when (status) {
        402 -> NetworkError.PaymentRequired()
        429 -> NetworkError.RateLimit(retryAfterSeconds = retryAfter?.toLongOrNull())
        502 -> NetworkError.BadGateway()
        else -> NetworkError.HttpError(status, description)
    }

    companion object {
        private const val STATUS_UNPROCESSABLE_ENTITY = 422

        /** World News API: стандартный маппинг HTTP-статусов. */
        fun worldNews(): NetworkErrorMapper = NetworkErrorMapper()

        /** Translate Plus API: 422 означает ошибку перевода. */
        fun translatePlus(): NetworkErrorMapper = NetworkErrorMapper(
            statusOverrides = mapOf(
                STATUS_UNPROCESSABLE_ENTITY to { NetworkError.TranslateError() },
            ),
        )
    }
}
