package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_CURRENT_LEVEL
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_CURRENT_XP
import dev.alexmester.utils.statistic.StatisticUtils.xpForLevel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DataSourceAddXpTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataSource: UserPreferencesDataSource

    @Before
    fun setUp() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = mainDispatcherRule.scope,
            produceFile = {
                File.createTempFile("xp_test_datastore", ".preferences_pb").apply {
                    deleteOnExit()
                }
            },
        )
        dataSource = UserPreferencesDataSourceImpl(dataStore)
    }

    @Test
    fun `given empty store when addXp below threshold then keeps level and accumulates xp`() = runTest {
        val delta = 5f // < 10

        dataSource.addXp(delta)
        val prefs = dataSource.userPreferences.first()

        assertEquals(1, prefs.currentLevel)
        assertEquals(5f, prefs.currentXp, 0.0001f)
    }

    @Test
    fun `given exact threshold xp then level up with zero remainder`() = runTest {
        val delta = xpForLevel(1)

        dataSource.addXp(delta)
        val prefs = dataSource.userPreferences.first()

        assertEquals(2, prefs.currentLevel)
        assertEquals(0f, prefs.currentXp, 0.0001f)
    }

    @Test
    fun `given xp above threshold then level up and keep remainder`() = runTest {
        val delta = 40f

        dataSource.addXp(delta)
        val prefs = dataSource.userPreferences.first()

        assertEquals(2, prefs.currentLevel)
        assertEquals(30f, prefs.currentXp, 0.0001f)
    }

    @Test
    fun `given large xp then multiple level ups`() = runTest {
        val delta = 200f

        dataSource.addXp(delta)
        val prefs = dataSource.userPreferences.first()

        assertEquals(4, prefs.currentLevel)
        assertEquals(83f, prefs.currentXp, 1f)
    }

    @Test
    fun `given existing xp then accumulates correctly`() = runTest {
        dataSource.addXp(5f)
        dataSource.addXp(3f)

        val prefs = dataSource.userPreferences.first()

        assertEquals(1, prefs.currentLevel)
        assertEquals(8f, prefs.currentXp, 0.0001f)
    }

    @Test
    fun `given existing xp when threshold reached then levels up`() = runTest {
        dataSource.addXp(7f)
        dataSource.addXp(5f)

        val prefs = dataSource.userPreferences.first()

        assertEquals(2, prefs.currentLevel)
        assertEquals(2f, prefs.currentXp, 0.0001f)
    }

    @Test
    fun `given max level then no further leveling`() = runTest {
        dataStore.edit {
            it[KEY_CURRENT_LEVEL] = 50
        }

        dataSource.addXp(1000f)
        val prefs = dataSource.userPreferences.first()

        assertEquals(50, prefs.currentLevel)
        assertEquals(0f, prefs.currentXp, 0.0001f)
    }
}