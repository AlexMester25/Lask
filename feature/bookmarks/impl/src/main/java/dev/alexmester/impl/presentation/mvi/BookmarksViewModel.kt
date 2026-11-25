package dev.alexmester.impl.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.utils.constants.LaskConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarksViewModel(
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<BookmarksState>(BookmarksState.Loading)
    val state: StateFlow<BookmarksState> = _state.asStateFlow()

    private val _sideEffects = Channel<BookmarksSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    /**
     * Растущее окно: запрос всегда ограничен [limit], а при прокрутке к концу
     * окно увеличивается на страницу. Так закладки не читаются из БД целиком.
     */
    private val limit = MutableStateFlow(LaskConstants.PAGE_SIZE)

    init {
        observeBookmarks()
    }

    fun handleIntent(intent: BookmarksIntent) {
        when (intent) {
            is BookmarksIntent.ConfirmDeletion -> confirmDeletion()
            is BookmarksIntent.CancelDeletion -> cancelDeletion()
            is BookmarksIntent.LoadMore -> loadMore()
            is BookmarksIntent.ArticleClick -> emitSideEffect(
                BookmarksSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
            )
            else -> _state.update { BookmarksReducer.reduce(it, intent) }
        }
    }

    private fun observeBookmarks() {
        combine(
            limit.flatMapLatest { articleRepository.observeBookmarkedArticles(it) },
            articleRepository.observeBookmarkedCount(),
        ) { articles, total -> articles to total }
            .onEach { (articles, total) ->
                _state.update { current ->
                    BookmarksReducer.onArticlesUpdated(
                        state = current,
                        articles = articles,
                        canLoadMore = articles.size < total,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadMore() {
        val content = _state.value.contentOrNull ?: return
        if (!content.canLoadMore || content.isLoadingMore) return

        _state.update { current ->
            (current as? BookmarksState.Content)?.copy(isLoadingMore = true) ?: current
        }
        limit.update { it + LaskConstants.PAGE_SIZE }
    }

    private fun confirmDeletion() {
        val content = _state.value.contentOrNull ?: return
        val idsToRemove = content.pendingRemovalIds

        if (idsToRemove.isEmpty()) {
            _state.update { BookmarksReducer.reduce(it, BookmarksIntent.ToggleEditMode) }
            return
        }

        viewModelScope.launch {
            _state.update { BookmarksReducer.onCloseEditMode(it) }
            articleRepository.removeBookmarks(idsToRemove)
        }
    }

    private fun cancelDeletion() {
        _state.update { BookmarksReducer.onCloseEditMode(it) }
    }

    private fun emitSideEffect(effect: BookmarksSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}