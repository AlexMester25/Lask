package dev.alexmester.impl.presentation

import android.util.Log
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
import dev.alexmester.newsfeed.impl.presentation.feed.contentOrNull
import dev.alexmester.newsfeed.impl.presentation.feed.isContent
import dev.alexmester.newsfeed.impl.presentation.feed.isOffline
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
import kotlinx.coroutines.flow.onStart
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

    private var lastKnownCountry: String? = null
    private var isFeedLoaded = false

    init {
        observeClustersWithPrefs()
    }

    fun handleIntent(intent: NewsFeedIntent) {
        _state.update { NewsFeedReducer.reduce(it, intent) }

        when (intent) {
            is NewsFeedIntent.Refresh -> refresh()
            is NewsFeedIntent.ArticleClick -> emitSideEffect(
                NewsFeedSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
        }
    }

    private fun observeClustersWithPrefs() {
        interactor.getClustersWithPrefsFlow()
            .onEach { (clusters, prefs) ->

                val countryChanged = lastKnownCountry != null &&
                lastKnownCountry != prefs.defaultCountry

                when {
                    countryChanged -> {
                        lastKnownCountry = prefs.defaultCountry
                        _state.update { NewsFeedScreenState.Loading }
                        loadFeed()
                    }
                    !isFeedLoaded -> {
                        // Первый раз — загружаем только один раз
                        lastKnownCountry = prefs.defaultCountry
                        isFeedLoaded = true
                        loadFeed()
                    }
                    _state.value.isOffline -> Unit

                    clusters.isNotEmpty() -> {
                        lastKnownCountry = prefs.defaultCountry
                        val lastCachedAt = interactor.getLastCachedAt()
                        _state.update {
                            NewsFeedReducer.onClustersLoaded(
                                clusters = clusters,
                                lastCachedAt = lastCachedAt,
                                country = prefs.defaultCountry,
                            )
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeClusters() {
        interactor.getClustersFlow()
            .onEach { clusters ->
                if (clusters.isEmpty()) return@onEach
                val currentState = _state.value
                if (currentState.isOffline) return@onEach

                val lastCachedAt = interactor.getLastCachedAt()
                val country = interactor.getCountry()
                _state.update {
                    NewsFeedReducer.onClustersLoaded(clusters, lastCachedAt, country)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observePreferencesChanges() {
        interactor.getPreferencesFlow()
            .drop(1)
            .distinctUntilChanged { old, new ->
                old.defaultCountry == new.defaultCountry &&
                        old.defaultLanguage == new.defaultLanguage
            }
            .onEach {
                _state.update { NewsFeedScreenState.Loading }
                loadFeed()
            }
            .launchIn(viewModelScope)
    }

    private fun loadFeed() {
        viewModelScope.launch {
            handleResult(interactor.refresh())
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            handleResult(interactor.refresh())
        }
    }

    private fun handleResult(result: AppResult<Unit>) {
        if (result is AppResult.Failure) {
            val currentState = _state.value
            val cachedClusters = currentState.contentOrNull?.clusters ?: emptyList()
            val lastCachedAt = currentState.contentOrNull?.lastCachedAt

            val (newState, message) = NewsFeedReducer.onNetworkError(
                state = currentState,
                error = result.error,
                cachedClusters = cachedClusters,
                lastCachedAt = lastCachedAt,
            )
            _state.update { newState }
            emitSideEffect(NewsFeedSideEffect.ShowError(message))
        }
    }

    private fun emitSideEffect(effect: NewsFeedSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}
