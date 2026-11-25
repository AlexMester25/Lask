package dev.alexmester.impl.presentation.mvi

import dev.alexmester.impl.FakeNewsFeedRepository
import dev.alexmester.impl.domain.model.RefreshFeedResult
import dev.alexmester.impl.domain.usecase.ObserveTrendsUseCase
import dev.alexmester.models.error.NetworkError
import dev.alexmester.models.preference.UserPreferences
import dev.alexmester.models.result.AppResult
import dev.alexmester.testing.MainDispatcherRule
import dev.alexmester.testing.builder.buildCluster
import dev.alexmester.testing.fake.FakeArticleRepository
import dev.alexmester.testing.fake.FakeUserPreferencesRepository
import dev.alexmester.ui.R
import dev.alexmester.ui.uitext.UiText
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsFeedViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val feedRepository = FakeNewsFeedRepository()
    private val preferencesRepository = FakeUserPreferencesRepository()
    private val articleRepository = FakeArticleRepository()

    @Test
    fun `cached clusters are shown after a successful initial refresh`() = runTest {
        val clusters = listOf(buildCluster(id = 1))
        feedRepository.emitClusters(clusters)
        val fixture = createFixture()

        assertEquals(NewsFeedState.Loading, fixture.viewModel.state.value)

        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Content(clusters = clusters, country = "us", language = "en"),
            fixture.viewModel.state.value,
        )
        assertEquals(listOf(false), feedRepository.refreshForces)
    }

    @Test
    fun `successful refresh without clusters produces empty state`() = runTest {
        feedRepository.refreshResult = AppResult.Success(RefreshFeedResult.Updated(0))
        val fixture = createFixture()

        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Empty(country = "us", language = "en"),
            fixture.viewModel.state.value,
        )
    }

    @Test
    fun `failed refresh without cached clusters produces error state and shows error effect`() =
        runTest {
            val error = NetworkError.Timeout()
            feedRepository.refreshResult = AppResult.Failure(error)
            val fixture = createFixture()

            advanceUntilIdle()

            assertEquals(
                NewsFeedState.Error(errorType = error),
                fixture.viewModel.state.value,
            )
            assertEquals(
                NewsFeedSideEffect.ShowError(error),
                fixture.sideEffects.single(),
            )
        }

    @Test
    fun `failed refresh keeps cached clusters on screen`() = runTest {
        val clusters = listOf(buildCluster(id = 1))
        val error = NetworkError.HttpError(code = 500)
        feedRepository.emitClusters(clusters)
        feedRepository.refreshResult = AppResult.Failure(error)
        val fixture = createFixture()

        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Content(clusters = clusters, country = "us", language = "en"),
            fixture.viewModel.state.value,
        )
        assertEquals(
            NewsFeedSideEffect.ShowError(error),
            fixture.sideEffects.single(),
        )
    }

    @Test
    fun `no internet keeps cached clusters and marks them offline`() = runTest {
        val clusters = listOf(buildCluster(id = 1))
        feedRepository.emitClusters(clusters)
        feedRepository.lastCachedAt = 123L
        feedRepository.refreshResult = AppResult.Failure(NetworkError.NoInternet())
        val fixture = createFixture()

        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Content(
                clusters = clusters,
                country = "us",
                language = "en",
                lastCachedAt = 123L,
                contentState = ContentState.Offline(lastCachedAt = 123L),
            ),
            fixture.viewModel.state.value,
        )
    }

    @Test
    fun `incompatible locale shows warning and keeps cached clusters`() = runTest {
        val clusters = listOf(buildCluster(id = 1))
        feedRepository.emitClusters(clusters)
        feedRepository.refreshResult =
            AppResult.Success(RefreshFeedResult.IncompatibleLocale)
        val fixture = createFixture()

        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Content(clusters = clusters, country = "us", language = "en"),
            fixture.viewModel.state.value,
        )
        val warning = fixture.sideEffects.single() as NewsFeedSideEffect.ShowWarning
        val message = warning.message as UiText.StringResource
        assertEquals(R.string.locale_incompatible, message.resId)
    }

    @Test
    fun `cached clusters stay visible while the initial refresh is running`() = runTest {
        val clusters = listOf(buildCluster(id = 1))
        val gate = CompletableDeferred<Unit>()
        feedRepository.emitClusters(clusters)
        feedRepository.refreshGate = gate
        val fixture = createFixture()

        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Content(
                clusters = clusters,
                country = "us",
                language = "en",
                contentState = ContentState.Refreshing,
            ),
            fixture.viewModel.state.value,
        )

        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Content(clusters = clusters, country = "us", language = "en"),
            fixture.viewModel.state.value,
        )
    }

    @Test
    fun `refresh intent forces a refresh`() = runTest {
        val fixture = createFixture()
        advanceUntilIdle()

        fixture.viewModel.handleIntent(NewsFeedIntent.Refresh)
        advanceUntilIdle()

        assertEquals(listOf(false, true), feedRepository.refreshForces)
    }

    /**
     * Material3-индикатор pull-to-refresh сворачивается сразу после жеста,
     * если контент уже загружен: повторный refresh не переводит состояние
     * в [ContentState.Refreshing] (это намеренное поведение).
     */
    @Test
    fun `manual refresh of loaded content does not mark it as refreshing`() = runTest {
        val clusters = listOf(buildCluster(id = 1))
        feedRepository.emitClusters(clusters)
        val fixture = createFixture()
        advanceUntilIdle()

        val gate = CompletableDeferred<Unit>()
        feedRepository.refreshGate = gate
        fixture.viewModel.handleIntent(NewsFeedIntent.Refresh)
        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Content(clusters = clusters, country = "us", language = "en"),
            fixture.viewModel.state.value,
        )

        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Content(clusters = clusters, country = "us", language = "en"),
            fixture.viewModel.state.value,
        )
    }

    @Test
    fun `article click emits navigation side effect`() = runTest {
        val fixture = createFixture()
        advanceUntilIdle()

        fixture.viewModel.handleIntent(
            NewsFeedIntent.ArticleClick(
                articleId = 42L,
                articleUrl = "https://example.com/42",
            )
        )
        advanceUntilIdle()

        assertEquals(
            NewsFeedSideEffect.NavigateToArticle(
                articleId = 42L,
                articleUrl = "https://example.com/42",
            ),
            fixture.sideEffects.single(),
        )
    }

    @Test
    fun `locale change triggers a forced refresh`() = runTest {
        createFixture()
        advanceUntilIdle()
        assertEquals(listOf(false), feedRepository.refreshForces)

        preferencesRepository.emit(
            UserPreferences(defaultCountry = "de", defaultLanguage = "de")
        )
        advanceUntilIdle()

        assertEquals(listOf(false, true), feedRepository.refreshForces)
    }

    @Test
    fun `preferences change without locale change does not refresh`() = runTest {
        createFixture()
        advanceUntilIdle()

        preferencesRepository.emit(
            UserPreferences(
                defaultCountry = "us",
                defaultLanguage = "en",
                isDarkTheme = true,
            )
        )
        advanceUntilIdle()

        assertEquals(listOf(false), feedRepository.refreshForces)
    }

    @Test
    fun `new refresh cancels the one still in flight`() = runTest {
        val error = NetworkError.Timeout()
        val gate = CompletableDeferred<Unit>()
        feedRepository.refreshGate = gate
        feedRepository.refreshResult = AppResult.Failure(error)
        val fixture = createFixture()
        advanceUntilIdle()
        assertEquals(1, feedRepository.refreshCallCount)

        fixture.viewModel.handleIntent(NewsFeedIntent.Refresh)
        advanceUntilIdle()

        assertEquals(2, feedRepository.refreshCallCount)
        assertEquals(1, feedRepository.cancelledRefreshes)

        gate.complete(Unit)
        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Error(errorType = error),
            fixture.viewModel.state.value,
        )
    }

    @Test
    fun `read article ids are exposed from the repository`() = runTest {
        val fixture = createFixture()
        advanceUntilIdle()
        assertTrue(fixture.viewModel.readArticleIds.value.isEmpty())

        articleRepository.emitReadArticleIds(setOf(1L, 2L))
        advanceUntilIdle()

        assertEquals(setOf(1L, 2L), fixture.viewModel.readArticleIds.value)
    }

    @Test
    fun `successful refresh after an error replaces the error state with content`() = runTest {
        val error = NetworkError.Timeout()
        feedRepository.refreshResult = AppResult.Failure(error)
        val fixture = createFixture()
        advanceUntilIdle()
        assertEquals(
            NewsFeedState.Error(errorType = error),
            fixture.viewModel.state.value,
        )

        val clusters = listOf(buildCluster(id = 1))
        feedRepository.emitClusters(clusters)
        feedRepository.refreshResult = AppResult.Success(RefreshFeedResult.Updated(1))
        fixture.viewModel.handleIntent(NewsFeedIntent.Refresh)
        advanceUntilIdle()

        assertEquals(
            NewsFeedState.Content(clusters = clusters, country = "us", language = "en"),
            fixture.viewModel.state.value,
        )
    }

    /**
     * Подписывается на потоки ViewModel до того, как `viewModelScope` начнёт
     * выполнять `init` (refresh + наблюдение за настройками). Без активной
     * подписки `WhileSubscribed` не запускает sharing, а `SharedFlow`
     * побочных эффектов теряет эмиссии.
     */
    private fun TestScope.createFixture(): Fixture {
        val viewModel = NewsFeedViewModel(
            userPreferencesRepository = preferencesRepository,
            articleRepository = articleRepository,
            newsFeedRepository = feedRepository,
            observeTrendsUseCase = ObserveTrendsUseCase(feedRepository, preferencesRepository),
        )
        val sideEffects = mutableListOf<NewsFeedSideEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.sideEffects.collect { sideEffects += it }
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.state.collect { }
        }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.readArticleIds.collect { }
        }
        return Fixture(viewModel = viewModel, sideEffects = sideEffects)
    }

    private class Fixture(
        val viewModel: NewsFeedViewModel,
        val sideEffects: List<NewsFeedSideEffect>,
    )
}
