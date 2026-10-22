package dev.alexmester.platform.locale

import dev.alexmester.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DeviceLocaleSynchronizer(
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    private val mutex = Mutex()

    /**
     * Приводит страну и язык приложения к системным, если пользователь не выбирал их вручную.
     * Идемпотентна: безопасно вызывать при старте и на каждом onResume.
     */
    suspend fun syncIfNeeded() {
        mutex.withLock {
            val prefs = userPreferencesRepository.observeUserPreferences().first()
            if (prefs.isLocaleManuallySet) return

            val country = deviceLocaleProvider.getCountry()
            val language = deviceLocaleProvider.getLanguage()
            if (prefs.defaultCountry != country || prefs.defaultLanguage != language) {
                userPreferencesRepository.initLocaleFromDevice(country, language)
            }
        }
    }
}