package dev.alexmester.lask.welcome_screen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.api.navigation.FeedRoute
import dev.alexmester.datastore.UserPreferencesDataSource
import dev.alexmester.datastore.util.DeviceLocaleProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SplashViewModel(
    private val preferencesDataSource: UserPreferencesDataSource,
    private val deviceLocaleProvider: DeviceLocaleProvider,
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesDataSource.userPreferences.collect { prefs ->

                if (!prefs.isLocaleManuallySet) {
                    _state.value = SplashState.Initializing

                    preferencesDataSource.initLocaleFromDevice(
                        country = deviceLocaleProvider.getCountry(),
                        language = deviceLocaleProvider.getLanguage(),
                    )
                }

                _state.value = SplashState.Ready(
                    isOnboardingCompleted = prefs.isOnboardingCompleted,
                )
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesDataSource.completeOnboarding()
        }
    }
}