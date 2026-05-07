package dev.alexmester.network.extension

import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
import dev.alexmester.network.error.NetworkErrorMapper
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> safeApiCall(
    mapper: NetworkErrorMapper,
    block: suspend () -> T
): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: NetworkError) {
        AppResult.Failure(e)
    } catch (e: Exception) {
        AppResult.Failure(mapper.map(e))
    }
}