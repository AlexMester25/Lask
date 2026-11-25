package dev.alexmester.datastore

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DataSourseStreakTest {

    private lateinit var dataSource: UserPreferencesDataSource

    private fun setupDataSource() {
        dataSource = UserPreferencesDataSourceImpl(InMemoryPreferencesDataStore())
    }

    private suspend fun prefs() = dataSource.userPreferences.first()

    @Test
    fun `should increment streak when yesterday`() = runTest {
        setupDataSource()

        dataSource.updateStreak("2026-04-24")
        dataSource.updateStreak("2026-04-25")

        val result = prefs()

        assertEquals(2, result.streakCount)
        assertEquals("2026-04-25", result.lastStreakDate)
    }
}
