package dev.alexmester.ui.components.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    isEditMode: Boolean,
    onProfileNameChange: (String) -> Unit,
    onEdit: () -> Unit,
    onApply: () -> Unit,
    onCancel: () -> Unit,
    onProfileAvatarClick: () -> Unit,
    onClappedArticleClick: () -> Unit,
    onReadArticleClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.LaskColors.backgroundPrimary)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        ProfileTopHeader(
            isEdit = isEditMode,
            onProfileAvatarClick = onProfileAvatarClick,
            onProfileNameChange = { onProfileNameChange(it) },
            onEdit = onEdit,
            onApply = onApply,
            onCancel = onCancel
        )
        Spacer(modifier = Modifier.height(24.dp))
        ProfileStatisticRow(
            modifier = Modifier,
            articleReadCount = 123,
            streakCount = 234,
            levelCount = 300
        )
        Spacer(modifier = Modifier.height(24.dp))
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(MaterialTheme.LaskColors.textSecondary)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Reading History",
            style = MaterialTheme.LaskTypography.h5,
            color = MaterialTheme.LaskColors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LaskRowMenu(
                modifier = Modifier,
                menuName = "Clapped Articles",
                onClick = onClappedArticleClick
            )
            LaskRowMenu(
                modifier = Modifier,
                menuName = "Read Articles",
                onClick = onReadArticleClick
            )
        }
    }
}

@Preview
@Composable
private fun ProfileContentPreviewDark() {
    LaskTheme(darkTheme = true) {
        ProfileContent(
            modifier = Modifier,
            isEditMode = true,
            onProfileNameChange = {},
            onEdit = {},
            onApply = {},
            onCancel = {},
            onProfileAvatarClick = {},
            onClappedArticleClick = {},
            onReadArticleClick = {}
        )
    }
}
@Preview
@Composable
private fun ProfileContentPreviewLight() {
    LaskTheme(darkTheme = false) {
        ProfileContent(
            modifier = Modifier,
            isEditMode = false,
            onProfileNameChange = {},
            onEdit = {},
            onApply = {},
            onCancel = {},
            onProfileAvatarClick = {},
            onClappedArticleClick = {},
            onReadArticleClick = {}
        )
    }
}