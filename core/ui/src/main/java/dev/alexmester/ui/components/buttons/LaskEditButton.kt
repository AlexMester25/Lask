package dev.alexmester.ui.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors

@Composable
fun LaskEditButton(
    modifier: Modifier = Modifier,
    tint: Color,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            modifier = Modifier,
            imageVector = ImageVector.vectorResource(R.drawable.ic_edit),
            contentDescription = null,
            tint = tint
        )
    }
}