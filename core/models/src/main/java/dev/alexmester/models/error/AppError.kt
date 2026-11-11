package dev.alexmester.models.error

/**
 * Общий предок ошибок приложения.
 *
 * Позволяет [dev.alexmester.models.result.AppResult] переносить любой сбой
 * в типизированном виде и не выдавать локальную ошибку за сетевую.
 * Наследуется от [Exception], чтобы ошибку можно было бросать и перехватывать.
 */
sealed class AppError : Exception()
