package dev.alexmester.impl.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.alexmester.impl.presentation.mvi.BookmarksIntent
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookmarksTopBar(
    isEditMode: Boolean,
    onIntent: (BookmarksIntent) -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.LaskColors.brand_blue10
        ),
        title = {
            Text(
                text = stringResource(R.string.tab_bookmark),
                style = MaterialTheme.LaskTypography.h3,
                color = MaterialTheme.LaskColors.textPrimary,
            )
        },
        actions = {
            AnimatedContent(targetState = isEditMode) { isEdit ->
                if (isEdit) {
                    Row {
                        TextButton(onClick = { onIntent(BookmarksIntent.CancelDeletion) }) {
                            Text(
                                text = stringResource(R.string.bookmarks_cancel),
                                style = MaterialTheme.LaskTypography.body2SemiBold,
                                color = MaterialTheme.LaskColors.error,
                            )
                        }
                        TextButton(onClick = { onIntent(BookmarksIntent.ConfirmDeletion) }) {
                            Text(
                                text = stringResource(R.string.bookmarks_done),
                                style = MaterialTheme.LaskTypography.body2SemiBold,
                                color = MaterialTheme.LaskColors.brand_blue,
                            )
                        }
                    }
                } else {
                    IconButton(onClick = { onIntent(BookmarksIntent.ToggleEditMode) }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.LaskColors.textPrimary,
                        )
                    }
                }
            }
        }
    )
}