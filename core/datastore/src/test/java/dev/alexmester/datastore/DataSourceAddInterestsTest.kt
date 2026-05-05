package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.File

private const val FILE_NAME = "add_interests_test_datastore"

@ExperimentalCoroutinesApi
class DataSourceAddInterestsTest {

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
    fun `addInterest adds new keyword`() = runTest {
            setupDataSource()

            dataSource.addInterest("Kotlin")
            advanceUntilIdle()

            val interests = dataSource.userPreferences.first().interests

            assertEquals(setOf("kotlin"), interests)
        }

    @Test
    fun `addInterest does not add duplicates`() = runTest {
            setupDataSource()

            dataSource.addInterest("kotlin")
            dataSource.addInterest("Kotlin")
            dataSource.addInterest("  Kotlin  ")
            advanceUntilIdle()

            val interests = dataSource.userPreferences.first().interests

            assertEquals(setOf("kotlin"), interests)
        }

    @Test
    fun `blank interest does not change datastore`() = runTest {
            setupDataSource()

            val before = dataSource.userPreferences.first().interests

            dataSource.addInterest("   ")
            dataSource.addInterest("")
            dataSource.addInterest("   \t\n")
            advanceUntilIdle()

            val after = dataSource.userPreferences.first().interests

            assertEquals(before, after)
        }

    @Test
    fun `addInterest accumulates multiple interests`() = runTest {
            setupDataSource()

            dataSource.addInterest("Kotlin")
            dataSource.addInterest("Android")
            dataSource.addInterest("Jetpack")
            advanceUntilIdle()

            val interests = dataSource.userPreferences.first().interests

            assertEquals(setOf("kotlin", "android", "jetpack"), interests)
        }

    @Test
    fun `addInterest is case insensitive`() = runTest {
            setupDataSource()

            dataSource.addInterest("Kotlin")
            dataSource.addInterest("kotlin")
            dataSource.addInterest("KOTLIN")
            advanceUntilIdle()

            val interests = dataSource.userPreferences.first().interests

            assertEquals(setOf("kotlin"), interests)
        }
}