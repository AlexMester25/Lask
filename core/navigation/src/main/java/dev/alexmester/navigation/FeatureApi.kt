package dev.alexmester.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

/**
 * Базовый контракт для всех Feature API.
 *
 * Каждый feature:xxx:api наследует этот интерфейс и добавляет
 * методы которые возвращают маршруты к своим экранам.
 *
 * Реализация живёт в feature:xxx:impl и регистрируется в Koin в :app.
 * Другие фичи зависят только от :api и получают реализацию через Koin.
 */
interface FeatureApi {
    fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    )
}