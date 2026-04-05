package dev.alexmester.impl.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.alexmester.newsfeed.impl.presentation.feed.NewsFeedScreenState
import dev.alexmester.newsfeed.impl.presentation.feed.contentOrNull
import dev.alexmester.ui.R
import dev.alexmester.ui.components.common.countryToFlag
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsFeedTopBar(state: NewsFeedScreenState) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.tab_top_news),
                    style = MaterialTheme.LaskTypography.h3,
                    color = MaterialTheme.LaskColors.textPrimary
                )
                Spacer(modifier = Modifier.width(12.dp))
                state.contentOrNull?.let { content ->
                    Text(
                        text = countryToFlag(content.country),
                        style = MaterialTheme.LaskTypography.h5
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.LaskColors.brand_blue10
        )
    )
}