package dev.alexmester.newsfeed.impl.presentation.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.alexmester.impl.presentation.components.NewsFeedList
import dev.alexmester.impl.presentation.components.NewsFeedTopBar
import dev.alexmester.impl.presentation.mvi.NewsFeedViewModel
import dev.alexmester.newsfeed.impl.presentation.components.NewsFeedOfflineBanner
import dev.alexmester.ui.components.error_screen.LaskErrorScreen
import dev.alexmester.ui.components.pull_to_refresh_box.LaskPullToRefreshBox
import dev.alexmester.ui.components.snackbar.LaskTopSnackbarHost
import dev.alexmester.ui.components.snackbar.showLaskSnackbar
import dev.alexmester.ui.desing_system.LaskColors
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun NewsFeedScreen(
    viewModel: NewsFeedViewModel = koinViewModel(),
    onArticleClick: (articleId: Long, articleUrl: String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val readArticleIds by viewModel.readArticleIds.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val stateRefreshBox = rememberPullToRefreshState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.sideEffects.collect { effect ->
            when (effect) {
                is NewsFeedSideEffect.ShowError -> {
                    snackbarHostState.showLaskSnackbar(
                        message = effect.message.asString(context),
                        isError = true,
                    )
                }
                is NewsFeedSideEffect.NavigateToArticle -> {
                    onArticleClick(effect.articleId, effect.articleUrl)
                }
            }
        }
    }

    NewsFeedScreenContent(
        modifier = Modifier,
        state = state,
        readArticleIds = readArticleIds,
        stateRefreshBox = stateRefreshBox,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::handleIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewsFeedScreenContent(
    modifier: Modifier,
    state: NewsFeedScreenState,
    readArticleIds: Set<Long>,
    stateRefreshBox: PullToRefreshState,
    snackbarHostState: SnackbarHostState,
    onIntent: (NewsFeedIntent) -> Unit,
) {

    Scaffold(
        topBar = { NewsFeedTopBar(state = state) }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.LaskColors.backgroundPrimary)
                .padding(paddingValues)
        ) {
            when (val currentState = state) {

                is NewsFeedScreenState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.LaskColors.brand_blue10,
                        trackColor = MaterialTheme.LaskColors.brand_blue
                    )
                }

                is NewsFeedScreenState.Error -> {
                    LaskErrorScreen(
                        modifier = Modifier,
                        errorMessage = currentState.message.asString(),
                        isRetrying = currentState.isRefreshing,
                        onRetry = { onIntent(NewsFeedIntent.Refresh) }
                    )
                }

                is NewsFeedScreenState.Content -> {
                    LaskPullToRefreshBox(
                        modifier = Modifier.fillMaxSize(),
                        isRefreshing = currentState.isRefreshing,
                        onRefresh = { onIntent(NewsFeedIntent.Refresh) },
                        state = stateRefreshBox,
                    ) {
                        Column {
                            AnimatedVisibility(visible = state.isOffline) {
                                if (state.contentState is ContentState.Offline) {
                                    NewsFeedOfflineBanner(
                                        lastCachedAt = state.contentState.lastCachedAt
                                    )
                                }
                            }
                            NewsFeedList(
                                modifier = Modifier,
                                state = currentState,
                                readArticleIds = readArticleIds,
                                bottomPadding = paddingValues.calculateBottomPadding(),
                                onClickArticle = { artilceId, arlicteUrl ->
                                    onIntent(
                                        NewsFeedIntent.ArticleClick(
                                            articleId = artilceId,
                                            articleUrl = arlicteUrl
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }

            LaskTopSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
            )
        }
    }
}






