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
        val prefs = dataStore.data.first()

        assertEquals(1, prefs[KEY_CURRENT_LEVEL])
        assertEquals(5f, prefs[KEY_CURRENT_XP]!!, 0.0001f)
    }

    @Test
    fun `given exact threshold xp then level up with zero remainder`() = runTest {
        val delta = xpForLevel(1)

        dataSource.addXp(delta)
        val prefs = dataStore.data.first()

        assertEquals(2, prefs[KEY_CURRENT_LEVEL])
        assertEquals(0f, prefs[KEY_CURRENT_XP]!!, 0.0001f)
    }

    @Test
    fun `given xp above threshold then level up and keep remainder`() = runTest {
        val delta = 40f

        dataSource.addXp(delta)
        val prefs = dataStore.data.first()

        assertEquals(2, prefs[KEY_CURRENT_LEVEL])
        assertEquals(30f, prefs[KEY_CURRENT_XP]!!, 0.0001f)
    }

    @Test
    fun `given large xp then multiple level ups`() = runTest {
        val delta = 200f

        dataSource.addXp(delta)
        val prefs = dataStore.data.first()

        // считаем вручную:
        // lvl1: 10 → осталось 190
        // lvl2: ~35 → осталось 155
        // lvl3: ~72 → осталось 83
        // lvl4: ~121 → НЕ хватает

        assertEquals(4, prefs[KEY_CURRENT_LEVEL])
        assertEquals(83f, prefs[KEY_CURRENT_XP]!!, 1f)
    }

    @Test
    fun `given existing xp then accumulates correctly`() = runTest {
        dataSource.addXp(5f)
        dataSource.addXp(3f)

        val prefs = dataStore.data.first()

        assertEquals(1, prefs[KEY_CURRENT_LEVEL])
        assertEquals(8f, prefs[KEY_CURRENT_XP]!!, 0.0001f)
    }

    @Test
    fun `given existing xp when threshold reached then levels up`() = runTest {
        dataSource.addXp(7f)
        dataSource.addXp(5f)

        val prefs = dataStore.data.first()

        assertEquals(2, prefs[KEY_CURRENT_LEVEL])
        assertEquals(2f, prefs[KEY_CURRENT_XP]!!, 0.0001f)
    }

    @Test
    fun `given max level then no further leveling`() = runTest {
        dataStore.edit {
            it[KEY_CURRENT_LEVEL] = 50
        }

        dataSource.addXp(1000f)
        val prefs = dataStore.data.first()

        assertEquals(50, prefs[KEY_CURRENT_LEVEL])
        assertEquals(0f, prefs[KEY_CURRENT_XP]!!, 0.0001f)
    }
}