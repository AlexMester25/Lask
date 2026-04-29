package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File


@OptIn(ExperimentalCoroutinesApi::class)
class DataSourseStreakTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataSource : UserPreferencesDataSource

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = dispatcherRule.scope,
            produceFile = {
                File.createTempFile("test_datastore", ".preferences_pb").apply {
                    deleteOnExit()
                }
            }
        )
        dataSource  = UserPreferencesDataSourceImpl(dataStore)
    }

    private suspend fun prefs() = dataSource.userPreferences.first()

    /**
     * Следует установить значение серии равным 1, если предыдущая дата отсутствует.
     */
    @Test
    fun `should set streak to 1 when no previous date`() = runTest {
        dataSource.updateStreak("2026-04-25")
        val result = prefs()

        assertEquals(1, result.streakCount)
        assertEquals("2026-04-25", result.lastStreakDate)
    }

    @Test
    fun `should not change streak when same day`() = runTest {
        dataSource.updateStreak("2026-04-25")
        dataSource.updateStreak("2026-04-25")
        val result = prefs()

        assertEquals(1, result.streakCount)
        assertEquals("2026-04-25", result.lastStreakDate)
    }

    @Test
    fun `should increment streak when yesterday`() = runTest {
        dataSource.updateStreak("2026-04-24")
        dataSource.updateStreak("2026-04-25")
        val result = prefs()

        assertEquals(2, result.streakCount)
        assertEquals("2026-04-25", result.lastStreakDate)
    }

    @Test
    fun `should reset streak when day skipped`() = runTest {
        dataSource.updateStreak("2026-04-20")
        dataSource.updateStreak("2026-04-25")
        val result = prefs()

        assertEquals(1, result.streakCount)
        assertEquals("2026-04-25", result.lastStreakDate)
    }

    @Test
    fun `should reset streak when stored date is invalid`() = runTest {
        dataSource.updateStreak("invalid-date")
        val result = prefs()

        assertEquals(1, result.streakCount)
    }
}