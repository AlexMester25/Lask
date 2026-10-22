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

private const val FILE_NAME = "xp_add_test_datastore"

@OptIn(ExperimentalCoroutinesApi::class)
class DataSourceAddXpTest {

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

    @Test
    fun `given existing xp when threshold reached then levels up`() = runTest {
        setupDataSource()

        dataSource.addXp(7f)
        dataSource.addXp(5f)
        advanceUntilIdle()

        val prefs = dataSource.userPreferences.first()

        assertEquals(2, prefs.currentLevel)
        assertEquals(2f, prefs.currentXp, 0.0001f)
    }

}