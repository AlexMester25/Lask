package dev.alexmester.models.result

import dev.alexmester.models.error.NetworkError
import kotlin.coroutines.cancellation.CancellationException


sealed class AppResult<out T> {

    data class Success<T>(val data: T) : AppResult<T>()

    data class Failure(val error: NetworkError) : AppResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isFailure: Boolean get() = this is Failure

    fun getOrNull(): T? = (this as? Success)?.data

    fun errorOrNull(): NetworkError? = (this as? Failure)?.error
}

// ── Extensions ────────────────────────────────────────────────────────────────

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onFailure(action: (NetworkError) -> Unit): AppResult<T> {
    if (this is AppResult.Failure) action(error)
    return this
}

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Failure -> this
}

inline fun <T> AppResult<T>.getOrElse(defaultValue: (NetworkError) -> T): T = when (this) {
    is AppResult.Success -> data
    is AppResult.Failure -> defaultValue(error)
}


suspend inline fun <T> runAppResult(crossinline block: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: NetworkError) {
        AppResult.Failure(e)
    } catch (e: Exception) {
        AppResult.Failure(NetworkError.Unknown(cause = e, message = e.message))
    }
}