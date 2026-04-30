package dev.alexmester.impl

import app.cash.turbine.test
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.models.news.NewsCluster
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObserveTrendsUseCaseTest {

    private val repository = FakeNewsFeedRepository()
    private lateinit var useCase: ObserveTrendsUseCase

    @Before
    fun setUp() {
        useCase = ObserveTrendsUseCase(
            repository = repository,
        )
    }

    @Test
    fun `should emit combined data when both flows have values`() = runTest {
        val clusters = listOf(buildCluster(1))
        val prefs = UserPreferences("de", "de")

        repository.emitClusters(clusters)
        repository.emitUserPreferences(prefs)

        useCase().test {
            val item = awaitItem()

            assertEquals(clusters, item.clusters)
            assertEquals(prefs, item.preferences)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit new value when clusters change`() = runTest {
        val prefs = UserPreferences("us", "en")
        repository.emitUserPreferences(prefs)

        val cluster1 = listOf(buildCluster(1))
        val cluster2 = listOf(buildCluster(2))

        repository.emitClusters(cluster1)

        useCase().test {
            val first = awaitItem()
            assertEquals(cluster1, first.clusters)

            repository.emitClusters(cluster2)

            val second = awaitItem()
            assertEquals(cluster2, second.clusters)
            assertEquals(prefs, second.preferences)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit new value when preferences change`() = runTest {
        val clusters = listOf(buildCluster(1))
        repository.emitClusters(clusters)

        val prefs1 = UserPreferences("us", "en")
        val prefs2 = UserPreferences("de", "de")

        repository.emitUserPreferences(prefs1)

        useCase().test {
            val first = awaitItem()
            assertEquals(prefs1, first.preferences)

            repository.emitUserPreferences(prefs2)

            val second = awaitItem()
            assertEquals(prefs2, second.preferences)
            assertEquals(clusters, second.clusters)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should always combine latest values`() = runTest {
        val cluster1 = listOf(buildCluster(1))
        val cluster2 = listOf(buildCluster(2))

        val prefs1 = UserPreferences("us", "en")
        val prefs2 = UserPreferences("fr", "fr")

        repository.emitClusters(cluster1)
        repository.emitUserPreferences(prefs1)

        useCase().test {
            val first = awaitItem()
            assertEquals(cluster1, first.clusters)
            assertEquals(prefs1, first.preferences)

            // 1️⃣ меняем clusters
            repository.emitClusters(cluster2)

            val second = awaitItem()
            assertEquals(cluster2, second.clusters)
            assertEquals(prefs1, second.preferences)

            // 2️⃣ меняем prefs
            repository.emitUserPreferences(prefs2)

            val third = awaitItem()
            assertEquals(cluster2, third.clusters)
            assertEquals(prefs2, third.preferences)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit immediately because stateflows have initial values`() = runTest {
        useCase().test {
            val item = awaitItem()

            assertEquals(emptyList<NewsCluster>(), item.clusters)
            assertEquals("us", item.preferences.defaultCountry)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should emit exact number of updates`() = runTest {
        val clusters = listOf(buildCluster(1))

        repository.emitClusters(clusters)

        useCase().test {
            awaitItem() // initial

            repository.emitUserPreferences(UserPreferences("de", "de"))
            awaitItem()

            repository.emitUserPreferences(UserPreferences("fr", "fr"))
            awaitItem()

            expectNoEvents()
            cancelAndIgnoreRemainingEvents()
        }
    }
}


