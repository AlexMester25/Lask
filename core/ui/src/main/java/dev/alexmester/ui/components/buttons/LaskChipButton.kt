package dev.alexmester.ui.components.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskPalette
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography

enum class LaskChipButtonVariants{
    Filters, Interests
}

@Composable
fun LaskChipButton(
    modifier: Modifier = Modifier,
    text: String,
    leadingLocaleIcon: String? = null,
    leadingIcon: ImageVector? = null,
    isSelected: Boolean = false,
    variant: LaskChipButtonVariants = LaskChipButtonVariants.Filters,
    onClick: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val shape = RoundedCornerShape(50.dp)
    val background = if (isSelected) MaterialTheme.LaskColors.brand_blue
        else MaterialTheme.LaskColors.backgroundPrimary
    val borderColor = if (isSelected) Color.Transparent else MaterialTheme.LaskColors.brand_blue10

    Row(
        modifier = modifier
            .clip(shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .background(background)
            .clickable(onClick = onClick)
            .animateContentSize()
            .padding(vertical = 8.dp)
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AnimatedContent(
            targetState = isSelected,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                            slideOutVertically { height -> height } + fadeOut()
                }.using(
                    SizeTransform(clip = false)
                )
            },
            label = "chip_content",
        ) { selected ->
            if (selected) {
                SelectedChipContent(
                    text = text,
                    localeIcon = leadingLocaleIcon,
                    leadingIcon = leadingIcon,
                    onDismiss = onDismiss,
                )
            } else {
                UnselectedChipContent(
                    text = text,
                    variant = variant,
                    onDismiss = onDismiss,
                )
            }
        }
    }
}


@Composable
private fun ChipLeadingContent(
    localeIcon: String?,
    icon: ImageVector?,
) {
    localeIcon?.let {
        Text(
            text = it,
            style = MaterialTheme.LaskTypography.footnote,
        )
    }
    icon?.let {
        Icon(
            imageVector = it,
            contentDescription = null,
            tint = LaskPalette.TextPrimaryDark,
        )
    }
}

@Composable
private fun ChipDismissIcon(
    size: Dp = 20.dp,
    onDismiss: () -> Unit,
) {
    Icon(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onDismiss),
        imageVector = ImageVector.vectorResource(R.drawable.ic_cancel),
        contentDescription = null,
        tint = LaskPalette.TextPrimaryDark,
    )
}


@Composable
private fun SelectedChipContent(
    text: String,
    localeIcon: String?,
    leadingIcon: ImageVector?,
    onDismiss: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ChipLeadingContent(localeIcon = localeIcon, icon = leadingIcon)
        Text(
            text = text,
            style = MaterialTheme.LaskTypography.body1SemiBold,
            color = MaterialTheme.LaskColors.textPrimary,
        )
        ChipDismissIcon(size = 20.dp, onDismiss = onDismiss)
    }
}

@Composable
private fun UnselectedChipContent(
    text: String,
    variant: LaskChipButtonVariants,
    onDismiss: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.LaskTypography.body1SemiBold,
            color = MaterialTheme.LaskColors.textPrimary,
        )
        when (variant) {
            LaskChipButtonVariants.Filters -> Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.LaskColors.textPrimary,
            )
            LaskChipButtonVariants.Interests -> ChipDismissIcon(size = 16.dp, onDismiss = onDismiss)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LaskChipButtonPreviewSeleceted1() {
    LaskTheme(darkTheme = true) {
        LaskChipButton(
            modifier = Modifier,
            text = "United State",
            leadingLocaleIcon = "USA",
            leadingIcon = null,
            isSelected = true,
            onClick = { },
            onDismiss = { }
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun LaskChipButtonPreviewSeleceted2() {
    LaskTheme(darkTheme = true) {
        LaskChipButton(
            modifier = Modifier,
            text = "Science",
            leadingLocaleIcon = null,
            leadingIcon = Icons.Default.Category,
            isSelected = true,
            onClick = { },
            onDismiss = { }
        )
    }
}
@Preview(showBackground = true)
@Composable
private fun LaskChipButtonPreviewUnSeleceted() {
    LaskTheme(darkTheme = false) {
        LaskChipButton(
            modifier = Modifier,
            text = "Country",
            leadingLocaleIcon = null,
            leadingIcon = null,
            isSelected = false,
            onClick = { },
            onDismiss = { }
        )
    }
}