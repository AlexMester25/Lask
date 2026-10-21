package dev.alexmester.platform.locale

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dev.alexmester.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LocaleChangeObserver(
    private val context: Context,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val scope: CoroutineScope,
) {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
                scope.launch {
                    val prefs = userPreferencesRepository.observeUserPreferences().first()
                    if (!prefs.isLocaleManuallySet) {
                        userPreferencesRepository.initLocaleFromDevice(
                            country = deviceLocaleProvider.getCountry(),
                            language = deviceLocaleProvider.getLanguage(),
                        )
                    }
                }
            }
        }
    }

    fun register() {
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_LOCALE_CHANGED))
    }

    fun unregister() {
        context.unregisterReceiver(receiver)
    }

    fun checkAndUpdate() {
        scope.launch {
            val prefs = userPreferencesRepository.observeUserPreferences().first()
            if (prefs.isLocaleManuallySet) return@launch

            val currentCountry = deviceLocaleProvider.getCountry()
            val currentLanguage = deviceLocaleProvider.getLanguage()

            if (prefs.defaultCountry != currentCountry ||
                prefs.defaultLanguage != currentLanguage
            ) {
                userPreferencesRepository.initLocaleFromDevice(
                    country = currentCountry,
                    language = currentLanguage,
                )
            }
        }
    }
}