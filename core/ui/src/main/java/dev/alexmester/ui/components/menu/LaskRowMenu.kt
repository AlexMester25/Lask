package dev.alexmester.ui.components.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.alexmester.ui.design_system.LaskColors
import dev.alexmester.ui.design_system.LaskTheme
import dev.alexmester.ui.design_system.LaskTypography

@Composable
fun LaskRowMenu(
    modifier: Modifier = Modifier,
    menuName: String,
    onClick: () -> Unit
) {
   Row(
       modifier = modifier
           .fillMaxWidth()
           .background(MaterialTheme.LaskColors.backgroundPrimary)
           .clickable(onClick = onClick)
           .padding(vertical = 12.dp),
       verticalAlignment = Alignment.CenterVertically,
       horizontalArrangement = Arrangement.SpaceBetween
   ){
       Text(
           text = menuName,
           style = MaterialTheme.LaskTypography.body1,
           color = MaterialTheme.LaskColors.textPrimary,
       )
       Icon(
           imageVector = Icons.Default.ArrowForwardIos,
           contentDescription = null,
           tint = MaterialTheme.LaskColors.textPrimary
       )
   }
}

@Preview(name = "Dark menu", showBackground = true,)
@Composable
private fun LaskRowMenuPreviewDark() {
    LaskTheme(darkTheme = true) {
        LaskRowMenu(
            modifier = Modifier,
            menuName = "Clapped Article",
            onClick = {}
        )
    }
}
@Preview(name = "Light menu", showBackground = true,)
@Composable
private fun LaskRowMenuPreviewLight() {
    LaskTheme(darkTheme = false) {
        LaskRowMenu(
            modifier = Modifier,
            menuName = "Clapped Article",
            onClick = {}
        )
    }
}