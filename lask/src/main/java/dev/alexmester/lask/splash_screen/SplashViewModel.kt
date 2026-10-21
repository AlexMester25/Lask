package dev.alexmester.lask.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.platform.locale.DeviceLocaleProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val deviceLocaleProvider: DeviceLocaleProvider,
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state

    private var isInitialized = false

    init {
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            userPreferencesRepository.observeUserPreferences().collect { prefs ->
                if (isInitialized) return@collect
                if (!prefs.isLocaleManuallySet) {
                    _state.value = SplashState.Initializing
                    userPreferencesRepository.initLocaleFromDevice(
                        country = deviceLocaleProvider.getCountry(),
                        language = deviceLocaleProvider.getLanguage(),
                    )
                }
                isInitialized = true
                _state.value = SplashState.Ready(
                    isOnboardingCompleted = prefs.isOnboardingCompleted,
                )
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.completeOnboarding()
        }
    }
}