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

private const val FILE_NAME = "remove_interests_test_datastore"

@ExperimentalCoroutinesApi
@OptIn(ExperimentalCoroutinesApi::class)
class DataSourceRemoveInterestsTest {

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
    fun `removeInterest removes existing keyword`() = runTest {
            setupDataSource()

            dataSource.addInterest("Kotlin")
            dataSource.addInterest("Android")
            dataSource.addInterest("Jetpack")
            advanceUntilIdle() // Ждем завершения всех записей

            dataSource.removeInterest("android")
            advanceUntilIdle()

            val interests = dataSource.userPreferences.first().interests
            assertEquals(setOf("kotlin", "jetpack"), interests)
        }

    @Test
    fun `removeInterest does nothing if keyword not exists`() = runTest {
            setupDataSource()

            dataSource.addInterest("Kotlin")
            dataSource.addInterest("Android")
            advanceUntilIdle()

            val before = dataSource.userPreferences.first().interests

            dataSource.removeInterest("Flutter")
            advanceUntilIdle()

            val after = dataSource.userPreferences.first().interests
            assertEquals(before, after)
        }

    @Test
    fun `removeInterest is case insensitive`() = runTest {
            setupDataSource()

            dataSource.addInterest("Kotlin")
            dataSource.addInterest("Android")
            advanceUntilIdle()

            dataSource.removeInterest("kotlin")
            advanceUntilIdle()

            val interests = dataSource.userPreferences.first().interests
            assertEquals(setOf("android"), interests)
        }

    @Test
    fun `removeInterest handles blank input gracefully`() = runTest {
            setupDataSource()

            dataSource.addInterest("Kotlin")
            dataSource.addInterest("Android")
            advanceUntilIdle()

            val before = dataSource.userPreferences.first().interests

            dataSource.removeInterest("   ")
            dataSource.removeInterest("")
            advanceUntilIdle()

            val after = dataSource.userPreferences.first().interests
            assertEquals(before, after)
        }

    @Test
    fun `removeInterest removes only one occurrence even if somehow duplicated`() = runTest {
            setupDataSource()

            dataSource.addInterest("kotlin")
            dataSource.addInterest("android")
            advanceUntilIdle()

            dataSource.removeInterest("kotlin")
            advanceUntilIdle()

            val interests = dataSource.userPreferences.first().interests
            assertEquals(setOf("android"), interests)
        }
}
