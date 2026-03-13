package dev.alexmester.lask

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.alexmester.posts.presentation.list.PostsScreen
import dev.alexmester.ui.desing_system.LaskColors
import dev.alexmester.ui.desing_system.LaskTypography
import dev.alexmester.users.presentation.list.UsersScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.LaskColors.backgroundPrimary,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.LaskColors.backgroundPrimary
            ) {
                NavigationBarItem(
                    icon = {},
                    label = {
                        Text(
                            text = "Posts",
                            style = MaterialTheme.LaskTypography.body1SemiBold,
                            color = MaterialTheme.LaskColors.textPrimary
                        )
                    },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        navController.navigate("posts") {
                            popUpTo("posts") { inclusive = true }
                        }
                    }
                )
                NavigationBarItem(
                    icon = {},
                    label = {
                        Text(
                            text = "Users",
                            style = MaterialTheme.LaskTypography.body1SemiBold,
                            color = MaterialTheme.LaskColors.textPrimary
                        )
                    },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        navController.navigate("users") {
                            popUpTo("users") { inclusive = true }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "posts",
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            composable("posts") {
                PostsScreen(
                    onPostClick = { postId ->
                        println("Post clicked: $postId")
                    }
                )
            }
            composable("users") {
                UsersScreen()
            }
        }
    }
}