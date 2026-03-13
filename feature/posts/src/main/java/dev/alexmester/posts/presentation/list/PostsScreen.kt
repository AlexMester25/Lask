package dev.alexmester.posts.presentation.list

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.alexmester.posts.domain.model.Post
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTypography
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    onPostClick: (Int) -> Unit,
    viewModel: PostsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val topBarHeight = 64.dp
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val totalHeight = topBarHeight + statusBarHeight
    val topBarHeightPx = with(LocalDensity.current) { totalHeight.toPx() }

    var topBarOffsetPx by remember { mutableFloatStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                topBarOffsetPx = (topBarOffsetPx + available.y)
                    .coerceIn(-topBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.LaskColors.backgroundPrimary)
            .nestedScroll(nestedScrollConnection)
    ) {
        when (val state = uiState) {
            is PostsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = totalHeight + 16.dp,
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.posts) { post ->
                        PostItem(post = post, onClick = { onPostClick(post.id) })
                    }
                }
            }
            is PostsUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is PostsUiState.Empty -> {
                Text("No posts available", modifier = Modifier.align(Alignment.Center))
            }
            is PostsUiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Error: ${state.message}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.refreshPosts() }) {
                        Text("Retry")
                    }
                }
            }
        }
        TopAppBar(
            modifier = Modifier
                .offset { IntOffset(x = 0, y = topBarOffsetPx.roundToInt()) },
            title = {
                Text(
                    text = "Posts",
                    style = MaterialTheme.LaskTypography.h3,
                    color = MaterialTheme.LaskColors.textPrimary
                )
            },
            windowInsets = WindowInsets.statusBars,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.LaskColors.brand_blue10,
            ),
        )

        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.LaskColors.brand_blue10)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = post.title,
            style = MaterialTheme.LaskTypography.h5,
            color = MaterialTheme.LaskColors.textPrimary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = post.body,
            style = MaterialTheme.LaskTypography.footnote,
            color = MaterialTheme.LaskColors.textSecondary,
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "User ID: ${post.userId}",
            style = MaterialTheme.LaskTypography.footnote,
            color = MaterialTheme.LaskColors.success
        )
    }
}
