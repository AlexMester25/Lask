package dev.alexmester.impl

import dev.alexmester.impl.domain.model.RefreshFeedResult
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
    fun `given cache is fresh, returns CacheFresh`() = runTest {
        repository.refreshResult =
            AppResult.Success(RefreshFeedResult.CacheFresh)

        val result = useCase(force = false)

        assertTrue(result is AppResult.Success)
        assertEquals(
            RefreshFeedResult.CacheFresh,
            (result as AppResult.Success).data
        )
    }

    @Test
    fun `given feed updated successfully, returns Updated result`() = runTest {
        repository.refreshResult =
            AppResult.Success(RefreshFeedResult.Updated(25))

        val result = useCase(force = true)

        assertTrue(result is AppResult.Success)

        val success = result as AppResult.Success

        assertTrue(success.data is RefreshFeedResult.Updated)

        val updated = success.data as RefreshFeedResult.Updated

        assertEquals(25, updated.articleCount)
    }

    @Test
    fun `given incompatible locale, returns IncompatibleLocale`() = runTest {
        repository.refreshResult =
            AppResult.Success(RefreshFeedResult.IncompatibleLocale)

        val result = useCase(force = true)

        assertTrue(result is AppResult.Success)
        assertEquals(
            RefreshFeedResult.IncompatibleLocale,
            (result as AppResult.Success).data
        )
    }

    @Test
    fun `given repository returns NoInternet, propagates Failure`() = runTest {
        repository.refreshResult =
            AppResult.Failure(NetworkError.NoInternet())

        val result = useCase(force = true)

        assertTrue(result is AppResult.Failure)
        assertTrue(
            (result as AppResult.Failure).error is NetworkError.NoInternet
        )
    }

    @Test
    fun `given repository returns RateLimit, propagates Failure`() = runTest {
        repository.refreshResult =
            AppResult.Failure(
                NetworkError.RateLimit(retryAfterSeconds = 60)
            )

        val result = useCase(force = true)

        val failure = result as AppResult.Failure

        assertTrue(failure.error is NetworkError.RateLimit)

        assertEquals(
            60L,
            (failure.error as NetworkError.RateLimit).retryAfterSeconds
        )
    }

    @Test
    fun `when invoked multiple times, delegates to repository each time`() = runTest {
        repository.refreshResult =
            AppResult.Success(RefreshFeedResult.CacheFresh)

        useCase(force = false)
        useCase(force = true)

        assertEquals(2, repository.refreshCallCount)
    }

    @Test
    fun `passes force parameter to repository`() = runTest {
        repository.refreshResult =
            AppResult.Success(RefreshFeedResult.CacheFresh)

        useCase(force = true)

        assertTrue(repository.lastForce)
    }
}