package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import dev.alexmester.datastore.model.UserPreferencesKeys.KEY_INTERESTS
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class DataSourceAddInterestsTest {

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
    fun `addInterest adds new keyword`() = runTest {
        dataSource.addInterest("Kotlin")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("kotlin"), interests)
    }

    @Test
    fun `addInterest does not add duplicates`() = runTest {
        dataSource.addInterest("kotlin")
        dataSource.addInterest("Kotlin")
        dataSource.addInterest("  Kotlin  ")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("kotlin"), interests)
    }

    @Test
    fun `blank interest does not change datastore`() = runTest {
        val before = dataSource.userPreferences.first().interests

        dataSource.addInterest("   ")
        dataSource.addInterest("")
        dataSource.addInterest("   \t\n")

        val after = dataSource.userPreferences.first().interests

        assertEquals(before, after)
    }

    @Test
    fun `addInterest accumulates multiple interests`() = runTest {
        dataSource.addInterest("Kotlin")
        dataSource.addInterest("Android")
        dataSource.addInterest("Jetpack")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("kotlin", "android", "jetpack"), interests)
    }

    @Test
    fun `addInterest is case insensitive`() = runTest {
        dataSource.addInterest("Kotlin")
        dataSource.addInterest("kotlin")
        dataSource.addInterest("KOTLIN")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("kotlin"), interests)
    }
}