package dev.alexmester.models.error


sealed class NetworkError : Exception() {

    class NoInternet : NetworkError()

    class Unauthorized : NetworkError()

    class Timeout : NetworkError()

    data class HttpError(
        val code: Int,
        override val message: String? = null
    ) : NetworkError()

    data class RateLimit(
        val retryAfterSeconds: Long? = null
    ) : NetworkError()

    data class ParseError(
        override val cause: Throwable? = null
    ) : NetworkError()

    data class Unknown(
        override val cause: Throwable? = null,
        override val message: String? = null
    ) : NetworkError()
}