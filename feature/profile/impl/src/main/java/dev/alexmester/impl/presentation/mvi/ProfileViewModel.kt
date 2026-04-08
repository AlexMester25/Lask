package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.database.dao.ReadingHistoryDao
import dev.alexmester.datastore.UserPreferencesDataSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileViewModel(
    private val preferencesDataSource: UserPreferencesDataSource,
    private val readingHistoryDao: ReadingHistoryDao,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _sideEffects = Channel<ProfileSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        observeProfile()
        updateStreak()
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.UpdateName -> saveName(intent.name)
            is ProfileIntent.UpdateAvatar -> saveAvatar(intent.uri)
            ProfileIntent.NavigateToReadArticles ->
                emitSideEffect(ProfileSideEffect.NavigateToArticleList(ArticleListType.READ))
            ProfileIntent.NavigateToClappedArticles ->
                emitSideEffect(ProfileSideEffect.NavigateToArticleList(ArticleListType.CLAPPED))
        }
    }

    private fun observeProfile() {
        preferencesDataSource.userPreferences
            .combine(readingHistoryDao.getReadCount()) { prefs, readCount ->
                _state.update { current ->
                    current.copy(
                        profileName = prefs.profileName,
                        avatarUri = prefs.avatarUri,
                        streakCount = prefs.streakCount,
                        currentLevel = prefs.currentLevel,
                        currentXp = prefs.currentXp,
                        articleReadCount = readCount,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateStreak() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            preferencesDataSource.updateStreak(today)
        }
    }

    private fun saveName(name: String) {
        viewModelScope.launch {
            preferencesDataSource.updateProfileName(name)
        }
    }

    private fun saveAvatar(uri: String?) {
        viewModelScope.launch {
            preferencesDataSource.updateAvatarUri(uri)
        }
    }

    private fun emitSideEffect(effect: ProfileSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}