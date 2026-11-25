package dev.alexmester.impl.presentation.mvi

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
class BookmarksViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeArticleRepository()

    @Test
    fun `first page is limited and allows loading more`() = runTest {
        repository.setBookmarks(articles(1L..25L))

        val viewModel = BookmarksViewModel(repository)
        advanceUntilIdle()

        val content = viewModel.state.value as BookmarksState.Content
        assertEquals(20, content.articles.size)
        assertTrue(content.canLoadMore)
        assertFalse(content.isLoadingMore)
    }

    @Test
    fun `load more grows the window until everything is loaded`() = runTest {
        repository.setBookmarks(articles(1L..25L))
        val viewModel = BookmarksViewModel(repository)
        advanceUntilIdle()

        viewModel.handleIntent(BookmarksIntent.LoadMore)
        advanceUntilIdle()

        val content = viewModel.state.value as BookmarksState.Content
        assertEquals(25, content.articles.size)
        assertFalse(content.canLoadMore)
        assertEquals(listOf(20, 40), repository.requestedLimits)
    }

    @Test
    fun `load more is ignored when everything is already loaded`() = runTest {
        repository.setBookmarks(articles(1L..5L))
        val viewModel = BookmarksViewModel(repository)
        advanceUntilIdle()

        viewModel.handleIntent(BookmarksIntent.LoadMore)
        advanceUntilIdle()

        val content = viewModel.state.value as BookmarksState.Content
        assertEquals(5, content.articles.size)
        assertFalse(content.canLoadMore)
        assertEquals(listOf(20), repository.requestedLimits)
    }

    @Test
    fun `empty bookmarks produce empty state`() = runTest {
        repository.setBookmarks(emptyList())

        val viewModel = BookmarksViewModel(repository)
        advanceUntilIdle()

        assertEquals(BookmarksState.Empty, viewModel.state.value)
    }

    private fun articles(ids: LongRange): List<NewsArticle> =
        ids.map { id -> buildArticle(id = id) }
}
