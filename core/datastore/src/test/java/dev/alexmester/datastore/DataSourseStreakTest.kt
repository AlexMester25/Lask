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
import java.util.UUID
import kotlin.io.path.createTempDirectory

private const val FILE_NAME = "streak_test_datastore"

@OptIn(ExperimentalCoroutinesApi::class)
class DataSourseStreakTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataSource: UserPreferencesDataSource

    private fun TestScope.setupDataSource() {
        val tempDir = createTempDirectory().toFile()
        val fileName = "${FILE_NAME}_${UUID.randomUUID()}"
        dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { File(tempDir, "$fileName.preferences_pb") }
        )
        dataSource = UserPreferencesDataSourceImpl(dataStore)
    }

    private suspend fun prefs() = dataSource.userPreferences.first()

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
}