package dev.alexmester.impl

import dev.alexmester.impl.domain.usecase.RefreshTrendsUseCase
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RefreshTrendsUseCaseTest {

    private val repository = FakeNewsFeedRepository()
    private lateinit var useCase: RefreshTrendsUseCase

    @Before
    fun setUp() {
        useCase = RefreshTrendsUseCase(
            repository = repository,
        )
    }

    @Test
    fun `given successful repo response, returns Success with item count`() = runTest {
        repository.refreshResult = AppResult.Success(5)

        val result = useCase()

        assertTrue(result is AppResult.Success)
        assertEquals(5, (result as AppResult.Success).data)
    }

    @Test
    fun `given repo returns NoInternet, propagates Failure`() = runTest {
        repository.refreshResult =
            AppResult.Failure(NetworkError.NoInternet())

        val result = useCase()

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is NetworkError.NoInternet)
    }

    @Test
    fun `given repo returns RateLimit, propagates Failure`() = runTest {
        repository.refreshResult =
            AppResult.Failure(NetworkError.RateLimit(retryAfterSeconds = 60))

        val result = useCase()

        val failure = result as AppResult.Failure
        assertTrue(failure.error is NetworkError.RateLimit)
        assertEquals(60L, (failure.error as NetworkError.RateLimit).retryAfterSeconds)
    }

    @Test
    fun `when called sequentially, increments refresh count`() = runTest {
        repository.refreshResult = AppResult.Success(10)

        useCase()
        useCase()

        assertEquals(2, repository.refreshCallCount)
    }
}