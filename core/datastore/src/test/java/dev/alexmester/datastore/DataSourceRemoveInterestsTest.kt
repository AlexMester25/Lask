package dev.alexmester.datastore

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DataSourceRemoveInterestsTest {

    private lateinit var dataSource: UserPreferencesDataSource

    private fun setupDataSource() {
        dataSource = UserPreferencesDataSourceImpl(InMemoryPreferencesDataStore())
    }

    @Test
    fun `removeInterest removes existing keyword`() = runTest {
        setupDataSource()

        dataSource.addInterest("Kotlin")
        dataSource.addInterest("Android")
        dataSource.addInterest("Jetpack")

        dataSource.removeInterest("android")

        val interests = dataSource.userPreferences.first().interests
        assertEquals(setOf("kotlin", "jetpack"), interests)
    }

    @Test
    fun `removeInterest does nothing if keyword not exists`() = runTest {
        setupDataSource()

        dataSource.addInterest("Kotlin")
        dataSource.addInterest("Android")

        val before = dataSource.userPreferences.first().interests

        dataSource.removeInterest("Flutter")

        val after = dataSource.userPreferences.first().interests
        assertEquals(before, after)
    }

    @Test
    fun `removeInterest is case insensitive`() = runTest {
        setupDataSource()

        dataSource.addInterest("Kotlin")
        dataSource.addInterest("Android")

        dataSource.removeInterest("kotlin")

        val interests = dataSource.userPreferences.first().interests
        assertEquals(setOf("android"), interests)
    }

    @Test
    fun `removeInterest handles blank input gracefully`() = runTest {
        setupDataSource()

        dataSource.addInterest("Kotlin")
        dataSource.addInterest("Android")

        val before = dataSource.userPreferences.first().interests

        dataSource.removeInterest("   ")
        dataSource.removeInterest("")

        val after = dataSource.userPreferences.first().interests
        assertEquals(before, after)
    }

    @Test
    fun `removeInterest removes only one occurrence even if somehow duplicated`() = runTest {
        setupDataSource()

        dataSource.addInterest("kotlin")
        dataSource.addInterest("android")

        dataSource.removeInterest("kotlin")

        val interests = dataSource.userPreferences.first().interests
        assertEquals(setOf("android"), interests)
    }
}
