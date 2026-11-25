package dev.alexmester.datastore

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DataSourceAddXpTest {

    private lateinit var dataSource: UserPreferencesDataSource

    private fun setupDataSource() {
        dataSource = UserPreferencesDataSourceImpl(InMemoryPreferencesDataStore())
    }

    @Test
    fun `given existing xp when threshold reached then levels up`() = runTest {
        setupDataSource()

        dataSource.addXp(7f)
        dataSource.addXp(5f)

        val prefs = dataSource.userPreferences.first()

        assertEquals(2, prefs.currentLevel)
        assertEquals(2f, prefs.currentXp, 0.0001f)
    }
}
