package dev.alexmester.impl.presentation.components.body

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography

@Composable
internal fun ArticleDetailCategory(category: String?) {
    category ?: return
    Text(
        modifier = Modifier
            .background(
                color = MaterialTheme.LaskColors.brand_blue.copy(alpha = 0.15f),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(vertical = 6.dp, horizontal = 8.dp),
        text = category.replaceFirstChar { it.uppercase() },
        style = MaterialTheme.LaskTypography.footnote,
        color = MaterialTheme.LaskColors.brand_blue,

    )
}