package dev.alexmester.ui.components.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTheme

@Composable
fun ProfileTopHeader(
    modifier: Modifier = Modifier,
    onProfileAvatarClick: () -> Unit,
    isEdit: Boolean,
    onProfileNameChange: (String) -> Unit,
    onEdit: () -> Unit,
    onApply: () -> Unit,
    onCancel: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.LaskColors.backgroundPrimary),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.LaskColors.brand_blue10),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(120.dp),
                contentScale = ContentScale.Crop,
                painter = rememberVectorPainter(ImageVector.vectorResource(R.drawable.ic_level_4)),
                contentDescription = null,
            )
            if (isEdit){
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.LaskColors.textLink, CircleShape)
                        .clickable{ onProfileAvatarClick() }
                        .background(
                            Brush.radialGradient(
                                colorStops = arrayOf(
                                    0.5f to Color.Transparent,
                                    1.0f to MaterialTheme.LaskColors.textLink
                                ),
                            ),
                            alpha = 0.3f,
                        ),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.LaskColors.textLink
                    )
                }
            }
        }

        ProfileNameRow(
            modifier = Modifier,
            profileName = "Dianne",
            levelData = Levels.LEVEL_5,
            isEdit = isEdit,
            onProfileNameChange = { onProfileNameChange(it) },
            onEdit = onEdit,
            onApply = onApply,
            onCancel = onCancel
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileTopHeaderPreview() {
    LaskTheme(darkTheme = true) {
        ProfileTopHeader(
            isEdit = false,
            onProfileAvatarClick = {},
            onProfileNameChange = { },
            onEdit = {},
            onApply = {},
            onCancel = {},
        )
    }
}