package dev.alexmester.network.ext

import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.error.NetworkErrorMapper

/**
 * Безопасная обёртка для всех API вызовов в data-слоях feature-модулей.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: NetworkError) {
        AppResult.Failure(e)
    } catch (e: Exception) {
        AppResult.Failure(NetworkErrorMapper.map(e))
    }
}