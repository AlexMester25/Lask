package dev.alexmester.datastore

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DataSourceAddInterestsTest {

    private lateinit var dataSource: UserPreferencesDataSource

    private fun setupDataSource() {
        dataSource = UserPreferencesDataSourceImpl(InMemoryPreferencesDataStore())
    }

    @Test
    fun `addInterest adds new keyword`() = runTest {
        setupDataSource()

        dataSource.addInterest("Kotlin")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("kotlin"), interests)
    }

    @Test
    fun `addInterest does not add duplicates`() = runTest {
        setupDataSource()

        dataSource.addInterest("kotlin")
        dataSource.addInterest("Kotlin")
        dataSource.addInterest("  Kotlin  ")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("kotlin"), interests)
    }

    @Test
    fun `blank interest does not change datastore`() = runTest {
        setupDataSource()

        val before = dataSource.userPreferences.first().interests

        dataSource.addInterest("   ")
        dataSource.addInterest("")
        dataSource.addInterest("   \t\n")

        val after = dataSource.userPreferences.first().interests

        assertEquals(before, after)
    }

    @Test
    fun `addInterest accumulates multiple interests`() = runTest {
        setupDataSource()

        dataSource.addInterest("Kotlin")
        dataSource.addInterest("Android")
        dataSource.addInterest("Jetpack")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("kotlin", "android", "jetpack"), interests)
    }

    @Test
    fun `addInterest is case insensitive`() = runTest {
        setupDataSource()

        dataSource.addInterest("Kotlin")
        dataSource.addInterest("kotlin")
        dataSource.addInterest("KOTLIN")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("kotlin"), interests)
    }
}
