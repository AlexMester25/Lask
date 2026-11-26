package dev.alexmester.impl.presentation.article_list.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.domain.articles.CategoryCount
import dev.alexmester.domain.articles.CategoryFilter
import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.utils.constants.LaskConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
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
class TypedArticleListViewModel(
    private val type: ArticleListType,
    private val articleRepository: ArticleRepository,
) : ViewModel() {

    /**
     * Фильтр и размер окна живут в одном потоке намеренно: при смене категории
     * окно сбрасывается атомарно. Раздельные `combine(limit, filter)` дали бы
     * лишнюю эмиссию — запрос новой категории со старым, уже разросшимся окном.
     */
    private data class Query(
        val filter: CategoryFilter = CategoryFilter.All,
        val limit: Int = LaskConstants.PAGE_SIZE,
    )

    private data class Snapshot(
        val filter: CategoryFilter,
        val articles: List<NewsArticle>,
        val facets: List<CategoryCount>,
    )

    private val _state = MutableStateFlow(TypedArticleListState())
    val state: StateFlow<TypedArticleListState> = _state.asStateFlow()

    private val _sideEffects = Channel<TypedArticleListSideEffect>(Channel.BUFFERED)
    val sideEffects = _sideEffects.receiveAsFlow()

    private val query = MutableStateFlow(Query())

    init {
        observeArticles()
    }

    fun handleIntent(intent: TypedArticleListIntent) {
        when (intent) {
            is TypedArticleListIntent.SelectFilter -> selectFilter(intent.filter)
            is TypedArticleListIntent.LoadMore -> loadMore()

            is TypedArticleListIntent.Back ->
                emitSideEffect(TypedArticleListSideEffect.NavigateBack)

            is TypedArticleListIntent.ArticleClick ->
                emitSideEffect(
                    TypedArticleListSideEffect.NavigateToArticle(intent.articleId, intent.articleUrl)
                )
        }
    }

    private fun observeArticles() {
        query
            .flatMapLatest { current ->
                combine(
                    articles(current),
                    categoryCounts(),
                ) { articles, facets -> Snapshot(current.filter, articles, facets) }
            }
            .onEach { snapshot ->
                val total = TypedArticleListState.totalOf(snapshot.filter, snapshot.facets)
                _state.update { current ->
                    current.copy(
                        articles = snapshot.articles,
                        categories = TypedArticleListState.categoriesOf(
                            facets = snapshot.facets,
                            selected = snapshot.filter,
                        ),
                        selectedFilter = snapshot.filter,
                        canLoadMore = snapshot.articles.size < total,
                        isLoading = false,
                        isLoadingMore = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun articles(query: Query): Flow<List<NewsArticle>> = when (type) {
        ArticleListType.READ -> articleRepository.observeReadArticles(query.filter, query.limit)
        ArticleListType.CLAPPED -> articleRepository.observeClappedArticles(query.filter, query.limit)
    }

    private fun categoryCounts(): Flow<List<CategoryCount>> = when (type) {
        ArticleListType.READ -> articleRepository.observeReadCategoryCounts()
        ArticleListType.CLAPPED -> articleRepository.observeClappedCategoryCounts()
    }

    private fun selectFilter(filter: CategoryFilter) {
        if (query.value.filter == filter) return
        query.update { Query(filter = filter, limit = LaskConstants.PAGE_SIZE) }
    }

    private fun loadMore() {
        val current = _state.value
        if (!current.canLoadMore || current.isLoadingMore) return

        _state.update { it.copy(isLoadingMore = true) }
        query.update { it.copy(limit = it.limit + LaskConstants.PAGE_SIZE) }
    }

    private fun emitSideEffect(effect: TypedArticleListSideEffect) {
        viewModelScope.launch { _sideEffects.send(effect) }
    }
}
