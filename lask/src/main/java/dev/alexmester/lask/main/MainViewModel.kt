package dev.alexmester.lask.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.domain.repository.UserPreferencesRepository
import dev.alexmester.platform.locale.DeviceLocaleSynchronizer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val localeSynchronizer: DeviceLocaleSynchronizer,
) : ViewModel() {

    /**
     * Eagerly: splash-экран читает .value в setKeepOnScreenCondition до появления подписчиков.
     * С WhileSubscribed значение осталось бы Loading и сплэш не снялся бы.
     * Синхронизация локали выполняется до первого Ready, как раньше в SplashViewModel.
     */
    val uiState: StateFlow<MainUiState> = userPreferencesRepository.observeUserPreferences()
        .map { prefs ->
            MainUiState.Ready(
                isDarkTheme = prefs.isDarkTheme,
                isOnboardingCompleted = prefs.isOnboardingCompleted,
            )
        }
        .onStart { localeSynchronizer.syncIfNeeded() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = MainUiState.Loading,
        )

    fun completeOnboarding() {
        viewModelScope.launch { userPreferencesRepository.completeOnboarding() }
    }

    /** Идемпотентно: внутри синхронизатора стоит Mutex. */
    fun onAppResumed() {
        viewModelScope.launch { localeSynchronizer.syncIfNeeded() }
    }
}