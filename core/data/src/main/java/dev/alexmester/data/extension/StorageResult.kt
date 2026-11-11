package dev.alexmester.data.extension

import dev.alexmester.data.error.StorageErrorMapper
import dev.alexmester.models.error.AppError
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.cancellation.CancellationException

/**
 * Выполняет операцию записи в локальное хранилище и приводит её сбой
 * к типизированной [AppError] (обычно [dev.alexmester.models.error.StorageError]).
 */
suspend inline fun <T> runStorageResult(crossinline block: suspend () -> T): AppResult<T> {
    return try {
        AppResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: AppError) {
        AppResult.Failure(e)
    } catch (e: Exception) {
        // Отмена может прийти как IOException/InterruptedException: не показываем её как ошибку записи.
        currentCoroutineContext().ensureActive()
        AppResult.Failure(StorageErrorMapper.map(e))
    }
}
