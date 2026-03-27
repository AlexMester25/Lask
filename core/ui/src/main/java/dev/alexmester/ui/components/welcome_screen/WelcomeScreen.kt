package dev.alexmester.ui.components.welcome_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTheme
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.ui.R
import dev.alexmester.ui.desing_system.LaskPalette

@Composable
fun WelcomeScreen(
    onExploreClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.65f)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.LaskColors.brand_blue,
                            LaskPalette.Brand_BlueLight10,
                        ),
                    )
                )
        )

        Image(
            painter = painterResource(id = R.drawable.welcome_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.TopCenter),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .background(
                    color = MaterialTheme.LaskColors.backgroundPrimary,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                ),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 52.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Get The Latest News\nAnd Updates",
                    style = MaterialTheme.LaskTypography.h3,
                    color = MaterialTheme.LaskColors.textPrimary,
                )
                Text(
                    textAlign = TextAlign.Center,
                    text = "From Politics to Entertainment: " +
                            "Your One-Stop Source for Comprehensive Coverage of the " +
                            "Latest News and Developments Across the Glob will be right " +
                            "on your hand.",
                    style = MaterialTheme.LaskTypography.body2,
                    color = MaterialTheme.LaskColors.textSecondary,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onExploreClick,
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.LaskColors.brand_blue,
                        contentColor = Color.White,
                    ),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Explore →",
                        style = MaterialTheme.LaskTypography.button1,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
private fun WelcomeScreenPreviewDark() {
    LaskTheme(darkTheme = true) {
        WelcomeScreen(onExploreClick = {})
    }
}
@Preview(showBackground = true, backgroundColor = 0xFF0D0D0D)
@Composable
private fun WelcomeScreenPreview() {
    LaskTheme(darkTheme = false) {
        WelcomeScreen(onExploreClick = {})
    }
}