package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_CURRENT_LEVEL
import dev.alexmester.utils.statistic.StatisticUtils.xpForLevel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

private const val FILE_NAME = "xp_add_test_datastore"

@OptIn(ExperimentalCoroutinesApi::class)
class DataSourceAddXpTest {

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

    @Test
    fun `given empty store when addXp below threshold then keeps level and accumulates xp`() = runTest {
            setupDataSource()

            dataSource.addXp(5f)
            advanceUntilIdle()

            val prefs = dataSource.userPreferences.first()

            assertEquals(1, prefs.currentLevel)
            assertEquals(5f, prefs.currentXp, 0.0001f)
        }

    @Test
    fun `given exact threshold xp then level up with zero remainder`() = runTest {
            setupDataSource()

            dataSource.addXp(xpForLevel(1))
            advanceUntilIdle()

            val prefs = dataSource.userPreferences.first()

            assertEquals(2, prefs.currentLevel)
            assertEquals(0f, prefs.currentXp, 0.0001f)
        }

    @Test
    fun `given xp above threshold then level up and keep remainder`() = runTest {
            setupDataSource()

            dataSource.addXp(40f)
            advanceUntilIdle()

            val prefs = dataSource.userPreferences.first()

            assertEquals(2, prefs.currentLevel)
            assertEquals(30f, prefs.currentXp, 0.0001f)
        }

    @Test
    fun `given large xp then multiple level ups`() = runTest {
            setupDataSource()

            dataSource.addXp(200f)
            advanceUntilIdle()

            val prefs = dataSource.userPreferences.first()

            assertEquals(4, prefs.currentLevel)
            assertEquals(83f, prefs.currentXp, 1f)
        }

    @Test
    fun `given existing xp then accumulates correctly`() = runTest {
            setupDataSource()

            dataSource.addXp(5f)
            dataSource.addXp(3f)
            advanceUntilIdle()

            val prefs = dataSource.userPreferences.first()

            assertEquals(1, prefs.currentLevel)
            assertEquals(8f, prefs.currentXp, 0.0001f)
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

    @Test
    fun `given max level then no further leveling`() = runTest {
            setupDataSource()

            dataStore.edit {
                it[KEY_CURRENT_LEVEL] = 50
            }
            advanceUntilIdle()

            dataSource.addXp(1000f)
            advanceUntilIdle()

            val prefs = dataSource.userPreferences.first()

            assertEquals(50, prefs.currentLevel)
            assertEquals(0f, prefs.currentXp, 0.0001f)
        }
}