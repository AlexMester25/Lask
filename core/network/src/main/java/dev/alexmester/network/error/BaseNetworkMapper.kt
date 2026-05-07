package dev.alexmester.network.error

import dev.alexmester.models.error.NetworkError
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException


abstract class BaseNetworkMapper : NetworkErrorMapper {
    override fun map(throwable: Throwable): NetworkError {
        check(throwable !is CancellationException) {
            "CancellationException must not be mapped"
        }
        return when (throwable) {
            is NetworkError -> throwable
            is HttpRequestTimeoutException -> NetworkError.Timeout()
            is UnknownHostException,
            is UnresolvedAddressException,
            is ConnectException,
            is SocketException -> NetworkError.NoInternet()
            is SerializationException -> NetworkError.ParseError(cause = throwable)
            is ResponseException -> mapHttpError(throwable)
            else -> NetworkError.Unknown(cause = throwable, message = throwable.message)
        }
    }

    protected open fun mapHttpError(throwable: ResponseException): NetworkError {
        val status = throwable.response.status.value
        return when (status) {
            402 -> NetworkError.PaymentRequired()
            502 -> NetworkError.BadGateway()
            429 -> {
                val retryAfter = throwable.response.headers["Retry-After"]?.toLongOrNull()
                NetworkError.RateLimit(retryAfterSeconds = retryAfter)
            }
            in 400..499 -> NetworkError.HttpError(status, throwable.response.status.description)
            in 500..599 -> NetworkError.HttpError(status, throwable.response.status.description)
            else -> NetworkError.HttpError(status)
        }
    }
}