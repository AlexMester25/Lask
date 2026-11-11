package dev.alexmester.data.extension

import dev.alexmester.data.error.NetworkErrorMapper
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.cancellation.CancellationException

/**
 * Выполняет сетевой вызов и приводит его сбой к типизированной [NetworkError].
 */
suspend fun <T> runNetworkResult(
    mapper: NetworkErrorMapper,
    block: suspend () -> T,
): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: NetworkError) {
        AppResult.Failure(e)
    } catch (e: Exception) {
        // Сокет, закрытый при отмене, приходит как IOException: не показываем отмену как сетевую ошибку.
        currentCoroutineContext().ensureActive()
        AppResult.Failure(mapper.map(e))
    }
}
