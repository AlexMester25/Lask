package dev.alexmester.impl.presentation.article_list.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.domain.repository.ArticleRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TypedArticleListViewModel(
    private val type: ArticleListType,
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(TypedArticleListState())
    val state: StateFlow<TypedArticleListState> = _state.asStateFlow()

    private val _sideEffects = Channel<TypedArticleListSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    init {
        observeArticles()
    }

    fun handleIntent(intent: TypedArticleListIntent) {
        when (intent) {
            is TypedArticleListIntent.SelectCategory ->
                _state.update { it.copy(selectedCategory = intent.category) }

            is TypedArticleListIntent.Back ->
                emitSideEffect(TypedArticleListSideEffect.NavigateBack)

            is TypedArticleListIntent.ArticleClick ->
                emitSideEffect(
                    TypedArticleListSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
                )
        }
    }

    private fun observeArticles() {
        val flow = when (type) {
            ArticleListType.READ -> articleRepository.observeReadArticles()
            ArticleListType.CLAPPED -> articleRepository.observeClappedArticles()
        }

        flow.onEach { articles ->
            _state.update { it.copy(allArticles = articles, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    private fun emitSideEffect(effect: TypedArticleListSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}