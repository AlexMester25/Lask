package dev.alexmester.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * In-memory [DataStore] для unit-тестов [UserPreferencesDataSourceImpl].
 *
 * Файловый `PreferenceDataStoreFactory` здесь непригоден: в JVM-тестах
 * `Build.VERSION.SDK_INT` равен 0, поэтому DataStore переименовывает `.tmp` через
 * `File.renameTo`, а тот на Windows не умеет перезаписывать существующий файл.
 * На устройстве (API 26+) используется `Files.move(REPLACE_EXISTING)`, поэтому
 * проблема проявляется только в тестах и только на Windows.
 *
 * Этот фейк проверяет логику источника данных, а не сериализацию DataStore.
 */
class InMemoryPreferencesDataStore(
    initial: Preferences = emptyPreferences(),
) : DataStore<Preferences> {

    private val state = MutableStateFlow(initial)
    private val mutex = Mutex()

    override val data: Flow<Preferences> = state.asStateFlow()

    override suspend fun updateData(
        transform: suspend (t: Preferences) -> Preferences,
    ): Preferences = mutex.withLock {
        val current = state.value
        val updated = transform(current)
        if (updated != current) {
            state.value = updated
        }
        updated
    }
}
