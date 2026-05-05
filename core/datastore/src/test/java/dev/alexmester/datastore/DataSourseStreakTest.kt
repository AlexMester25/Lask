package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

private const val FILE_NAME = "streak_test_datastore"

@OptIn(ExperimentalCoroutinesApi::class)
class DataSourseStreakTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataSource: UserPreferencesDataSource

    private fun TestScope.setupDataSource() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = {
                File.createTempFile(FILE_NAME, ".preferences_pb").apply {
                    deleteOnExit()
                }
            }
        )
        dataSource = UserPreferencesDataSourceImpl(dataStore)
    }

    private suspend fun prefs() = dataSource.userPreferences.first()

    @Test
    fun `should set streak to 1 when no previous date`() = runTest {
            setupDataSource()

            dataSource.updateStreak("2026-04-25")
            advanceUntilIdle()

            val result = prefs()

            assertEquals(1, result.streakCount)
            assertEquals("2026-04-25", result.lastStreakDate)
        }

    @Test
    fun `should not change streak when same day`() = runTest {
            setupDataSource()

            dataSource.updateStreak("2026-04-25")
            dataSource.updateStreak("2026-04-25")
            advanceUntilIdle()

            val result = prefs()

            assertEquals(1, result.streakCount)
            assertEquals("2026-04-25", result.lastStreakDate)
        }

    @Test
    fun `should increment streak when yesterday`() = runTest {
            setupDataSource()

            dataSource.updateStreak("2026-04-24")
            dataSource.updateStreak("2026-04-25")
            advanceUntilIdle()

            val result = prefs()

            assertEquals(2, result.streakCount)
            assertEquals("2026-04-25", result.lastStreakDate)
        }

    @Test
    fun `should reset streak when day skipped`() = runTest {
            setupDataSource()

            dataSource.updateStreak("2026-04-20")
            dataSource.updateStreak("2026-04-25")
            advanceUntilIdle()

            val result = prefs()

            assertEquals(1, result.streakCount)
            assertEquals("2026-04-25", result.lastStreakDate)
        }

    @Test
    fun `should reset streak when stored date is invalid`() = runTest {
            setupDataSource()

            dataSource.updateStreak("invalid-date")
            advanceUntilIdle()

            val result = prefs()

            assertEquals(1, result.streakCount)
        }
}