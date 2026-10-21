package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InterestsViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(InterestsState())
    val state: StateFlow<InterestsState> = _state.asStateFlow()

    private val _sideEffects = Channel<InterestsSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        userPreferencesRepository.observeUserPreferences().onEach { prefs ->
            _state.update { it.copy(interests = prefs.interests) }
        }.launchIn(viewModelScope)
    }

    fun handleIntent(intent: InterestsIntent) {
        when (intent) {
            is InterestsIntent.OnInputChange ->
                _state.update { it.copy(inputText = intent.text) }

            is InterestsIntent.Add -> addInterest()

            is InterestsIntent.Remove -> viewModelScope.launch {
                userPreferencesRepository.removeInterest(intent.keyword)
            }

            is InterestsIntent.Back ->
                viewModelScope.launch { _sideEffects.send(InterestsSideEffect.NavigateBack) }
        }
    }

    private fun addInterest() {
        viewModelScope.launch {
            userPreferencesRepository.addInterest(_state.value.inputText)
            _state.update { it.copy(inputText = "") }
        }
    }
}