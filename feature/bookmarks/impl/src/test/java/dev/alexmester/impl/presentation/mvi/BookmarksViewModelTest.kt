package dev.alexmester.impl.presentation.mvi

import dev.alexmester.domain.repository.ArticleRepository
import dev.alexmester.impl.MainDispatcherRule
import dev.alexmester.models.news.NewsArticle
import dev.alexmester.models.result.AppResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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

    private fun articles(ids: LongRange): List<NewsArticle> = ids.map { id ->
        NewsArticle(
            id = id,
            title = "Article $id",
            text = null,
            summary = null,
            url = "https://example.com/$id",
            image = null,
            video = null,
            publishDate = "2026-01-01",
            authors = emptyList(),
            category = null,
            language = null,
            sourceCountry = null,
            sentiment = null,
        )
    }
}

private class FakeArticleRepository : ArticleRepository {

    private val bookmarks = MutableStateFlow<List<NewsArticle>>(emptyList())
    private val bookmarksCount = MutableStateFlow(0)

    /** Ограничения, с которыми создавались потоки закладок. */
    val requestedLimits = mutableListOf<Int>()

    fun setBookmarks(articles: List<NewsArticle>) {
        bookmarks.value = articles
        bookmarksCount.value = articles.size
    }

    override fun observeBookmarkedArticles(limit: Int): Flow<List<NewsArticle>> {
        requestedLimits += limit
        return bookmarks.map { it.take(limit) }
    }

    override fun observeBookmarkedCount(): Flow<Int> = bookmarksCount

    override fun observeReadArticleIds(): Flow<Set<Long>> = flowOf(emptySet())

    override fun observeReadArticles(): Flow<List<NewsArticle>> = flowOf(emptyList())

    override fun observeClappedArticles(): Flow<List<NewsArticle>> = flowOf(emptyList())

    override suspend fun saveArticles(articles: List<NewsArticle>): AppResult<Unit> =
        AppResult.Success(Unit)

    override suspend fun getArticleById(id: Long): NewsArticle? = null

    override fun observeIsBookmarked(articleId: Long): Flow<Boolean> = flowOf(false)

    override suspend fun toggleBookmark(articleId: Long): Boolean = false

    override suspend fun removeBookmarks(ids: Set<Long>) = Unit

    override fun observeClapCount(articleId: Long): Flow<Int> = flowOf(0)

    override suspend fun addClap(articleId: Long) = Unit

    override suspend fun markAsRead(articleId: Long): Boolean = false

    override fun observeReadCount(): Flow<Int> = flowOf(0)
}
