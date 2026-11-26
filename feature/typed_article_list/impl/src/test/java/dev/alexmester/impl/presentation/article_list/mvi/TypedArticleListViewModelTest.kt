package dev.alexmester.impl.presentation.article_list.mvi

import dev.alexmester.api.navigation.ArticleListType
import dev.alexmester.domain.articles.CategoryFilter
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.testing.MainDispatcherRule
import dev.alexmester.testing.builder.buildArticle
import dev.alexmester.testing.fake.FakeArticleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TypedArticleListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeArticleRepository()

    @Test
    fun `first page is limited and allows loading more`() = runTest {
        repository.emitReadArticles(articles(1L..25L))

        val viewModel = viewModel(ArticleListType.READ)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(20, state.articles.size)
        assertTrue(state.canLoadMore)
        assertFalse(state.isLoading)
        assertEquals(listOf(20), repository.readRequestedLimits)
    }

    @Test
    fun `load more grows the window until everything is loaded`() = runTest {
        repository.emitReadArticles(articles(1L..25L))
        val viewModel = viewModel(ArticleListType.READ)
        advanceUntilIdle()

        viewModel.handleIntent(TypedArticleListIntent.LoadMore)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(25, state.articles.size)
        assertFalse(state.canLoadMore)
        assertEquals(listOf(20, 40), repository.readRequestedLimits)
    }

    @Test
    fun `load more is ignored when everything is already loaded`() = runTest {
        repository.emitReadArticles(articles(1L..5L))
        val viewModel = viewModel(ArticleListType.READ)
        advanceUntilIdle()

        viewModel.handleIntent(TypedArticleListIntent.LoadMore)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(5, state.articles.size)
        assertFalse(state.canLoadMore)
        assertEquals(listOf(20), repository.readRequestedLimits)
    }

    @Test
    fun `changing category filters the list and resets the window`() = runTest {
        repository.emitReadArticles(
            articles(1L..25L, category = "Tech") + articles(26L..30L, category = "Sport"),
        )
        val viewModel = viewModel(ArticleListType.READ)
        advanceUntilIdle()

        viewModel.handleIntent(TypedArticleListIntent.LoadMore)
        advanceUntilIdle()

        viewModel.handleIntent(
            TypedArticleListIntent.SelectFilter(CategoryFilter.Named("Sport"))
        )
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals((26L..30L).toList(), state.articles.map { it.id })
        assertEquals(CategoryFilter.Named("Sport"), state.selectedFilter)
        assertEquals(20, repository.readRequestedLimits.last())
        assertFalse(state.canLoadMore)
    }

    @Test
    fun `can load more uses the selected category facet instead of the global total`() = runTest {
        repository.emitReadArticles(
            articles(1L..25L, category = "Tech") + articles(26L..30L, category = "Sport"),
        )
        val viewModel = viewModel(ArticleListType.READ)
        advanceUntilIdle()

        viewModel.handleIntent(
            TypedArticleListIntent.SelectFilter(CategoryFilter.Named("Sport"))
        )
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(5, state.articles.size)
        assertFalse(state.canLoadMore)
    }

    @Test
    fun `categories come from facets with counts of the whole list`() = runTest {
        repository.emitReadArticles(
            articles(1L..25L, category = "Tech") + articles(26L..30L, category = "Sport"),
        )
        val viewModel = viewModel(ArticleListType.READ)
        advanceUntilIdle()

        assertEquals(
            listOf(
                CategoryFilter.All to 30,
                CategoryFilter.Named("Tech") to 25,
                CategoryFilter.Named("Sport") to 5,
            ),
            viewModel.state.value.categories,
        )
    }

    @Test
    fun `uncategorized filter returns only articles without category`() = runTest {
        repository.emitReadArticles(
            articles(1L..3L, category = null) + articles(4L..5L, category = "Tech"),
        )
        val viewModel = viewModel(ArticleListType.READ)
        advanceUntilIdle()

        viewModel.handleIntent(
            TypedArticleListIntent.SelectFilter(CategoryFilter.Uncategorized)
        )
        advanceUntilIdle()

        assertEquals(listOf(1L, 2L, 3L), viewModel.state.value.articles.map { it.id })
    }

    @Test
    fun `empty list produces an empty state without pagination`() = runTest {
        val viewModel = viewModel(ArticleListType.READ)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.isEmpty)
        assertFalse(state.canLoadMore)
        assertFalse(state.isLoading)
    }

    @Test
    fun `clapped list pages through the clapped queries`() = runTest {
        repository.emitClappedArticles(articles(1L..25L))
        val viewModel = viewModel(ArticleListType.CLAPPED)
        advanceUntilIdle()

        assertEquals(20, viewModel.state.value.articles.size)
        assertTrue(viewModel.state.value.canLoadMore)
        assertEquals(listOf(20), repository.clappedRequestedLimits)
        assertTrue(repository.readRequestedLimits.isEmpty())
    }

    private fun viewModel(type: ArticleListType) = TypedArticleListViewModel(
        type = type,
        articleRepository = repository,
    )

    private fun articles(
        ids: LongRange,
        category: String? = null,
    ): List<NewsArticle> = ids.map { id -> buildArticle(id = id, category = category) }
}
