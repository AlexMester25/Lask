package dev.alexmester.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.datastore.model.UserPreferences
import dev.alexmester.impl.domain.interactor.NewsFeedInteractor
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.result.AppResult
import dev.alexmester.newsfeed.impl.presentation.feed.ContentState
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedIntent
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedReducer
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedScreenState
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedSideEffect
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsFeedViewModel(
    private val interactor: NewsFeedInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow<NewsFeedScreenState>(NewsFeedScreenState.Loading)
    val state: StateFlow<NewsFeedScreenState> = _state.asStateFlow()

    private val _sideEffects = Channel<NewsFeedSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        observeClusters()
        observePreferencesChanges()
        loadFeed()
    }

    fun handleIntent(intent: NewsFeedIntent) {
        _state.update { NewsFeedReducer.reduce(it, intent) }

        when (intent) {
            is NewsFeedIntent.Refresh -> refresh()
            is NewsFeedIntent.ArticleClick -> navigateToArticle(intent)
        }
    }

    private fun observeClusters() {
        interactor.getClustersFlow().onEach { clusters ->
            val lastCachedAt = interactor.getLastCachedAt()
            val currentState = _state.value
            val country = interactor.getCountry()
            if (clusters.isEmpty()) return@onEach
            if (currentState !is NewsFeedScreenState.Content ||
                currentState.contentState !is ContentState.Offline
            ) {
                _state.update {
                    NewsFeedReducer.onClustersLoaded(clusters, lastCachedAt, country)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun observePreferencesChanges() {
        interactor.getPreferencesFlow()
            .drop(1)
            .distinctUntilChanged { old, new ->
                old.defaultCountry == new.defaultCountry &&
                old.defaultLanguage == new.defaultLanguage
            }
            .onEach { newPrefs ->
                _state.update { NewsFeedScreenState.Loading }
                loadFeed()
            }.launchIn(viewModelScope)
    }

    private fun loadFeed() {
        viewModelScope.launch {
            when (val result = interactor.refresh(forceRefresh = false)) {
                is AppResult.Success -> Unit
                is AppResult.Failure -> handleError(result.error)
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            when (val result = interactor.refresh(forceRefresh = true)) {
                is AppResult.Success -> Unit
                is AppResult.Failure -> handleError(result.error)
            }
        }
    }

    private fun navigateToArticle(intent: NewsFeedIntent.ArticleClick) {
        viewModelScope.launch {
            _sideEffects.send(
                NewsFeedSideEffect.NavigateToArticle(
                    articleId = intent.articleId,
                    articleUrl = intent.articleUrl,
                )
            )
        }
    }

    private fun handleError(error: NetworkError) {
        viewModelScope.launch {
            val currentState = _state.value
            when (error) {
                is NetworkError.NoInternet -> {
                    val message = UiText.StringResource(R.string.error_no_internet)
                    val clusters =
                        (currentState as? NewsFeedScreenState.Content)?.clusters ?: emptyList()
                    val lastCachedAt = interactor.getLastCachedAt()
                    _state.update {
                        NewsFeedReducer.onOffline(
                            clusters = clusters,
                            lastCachedAt = lastCachedAt,
                            message = message
                        )
                    }
                    _sideEffects.send(NewsFeedSideEffect.ShowError(message))
                }

                is NetworkError.PaymentRequired -> {
                    val message = UiText.StringResource(R.string.error_payment_required)
                    _sideEffects.send(NewsFeedSideEffect.ShowError(message))
                    _state.update { NewsFeedReducer.onError(currentState, message) }
                }

                is NetworkError.RateLimit -> {
                    val message = UiText.StringResource(R.string.error_rate_limit)
                    _state.update { NewsFeedReducer.onError(currentState, message) }
                    _sideEffects.send(NewsFeedSideEffect.ShowError(message))
                }

                else -> {
                    val message = UiText.StringResource(R.string.error_unknown)
                    _state.update { NewsFeedReducer.onError(currentState, message) }
                    _sideEffects.send(NewsFeedSideEffect.ShowError(message))
                }
            }
        }
    }
}