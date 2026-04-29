package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

class DataSourceRemoveInterestsTest {

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
            }
        )

        dataSource = UserPreferencesDataSourceImpl(dataStore)
    }

    @Test
    fun `removeInterest removes existing keyword`() = runTest {
        dataSource.addInterest("Kotlin")
        dataSource.addInterest("Android")
        dataSource.addInterest("Jetpack")

        dataSource.removeInterest("android")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("kotlin", "jetpack"), interests)
    }

    @Test
    fun `removeInterest does nothing if keyword not exists`() = runTest {
        dataSource.addInterest("Kotlin")
        dataSource.addInterest("Android")

        val before = dataSource.userPreferences.first().interests

        dataSource.removeInterest("Flutter")

        val after = dataSource.userPreferences.first().interests

        assertEquals(before, after)
    }

    @Test
    fun `removeInterest is case insensitive`() = runTest {
        dataSource.addInterest("Kotlin")
        dataSource.addInterest("Android")

        dataSource.removeInterest("kotlin")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("android"), interests)
    }

    @Test
    fun `removeInterest handles blank input gracefully`() = runTest {
        dataSource.addInterest("Kotlin")
        dataSource.addInterest("Android")

        val before = dataSource.userPreferences.first().interests

        dataSource.removeInterest("   ")
        dataSource.removeInterest("")
        dataSource.removeInterest("   \t")

        val after = dataSource.userPreferences.first().interests

        assertEquals(before, after)
    }

    @Test
    fun `removeInterest removes only one occurrence even if somehow duplicated`() = runTest {
        dataSource.addInterest("kotlin")
        dataSource.addInterest("android")

        dataSource.removeInterest("kotlin")

        val interests = dataSource.userPreferences.first().interests

        assertEquals(setOf("android"), interests)
    }
}