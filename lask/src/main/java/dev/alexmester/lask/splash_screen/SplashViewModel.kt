package dev.alexmester.lask.splash_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.platform.locale.DeviceLocaleSynchronizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val localeSynchronizer: DeviceLocaleSynchronizer,
) : ViewModel() {

    private val _state = MutableStateFlow<SplashState>(SplashState.Loading)
    val state: StateFlow<SplashState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            localeSynchronizer.syncIfNeeded()
            val prefs = userPreferencesRepository.observeUserPreferences().first()
            _state.value = SplashState.Ready(isOnboardingCompleted = prefs.isOnboardingCompleted)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch { userPreferencesRepository.completeOnboarding() }
    }
}