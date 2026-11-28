package dev.alexmester.impl.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.alexmester.api.navigation.LocalePickerRoute
import dev.alexmester.api.navigation.LocalePickerType
import dev.alexmester.api.navigation.SettingsApi
import dev.alexmester.api.navigation.SettingsRoute
import dev.alexmester.impl.presentation.locale_picker.LocalePickerScreen
import dev.alexmester.impl.presentation.settings.SettingsScreen

internal class SettingsImpl : SettingsApi {

    override fun settingsRoute(): SettingsRoute = SettingsRoute

    override fun localePickerRoute(type: LocalePickerType): LocalePickerRoute =
        LocalePickerRoute(type)

    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navController: NavHostController,
    ) {
        navGraphBuilder.composable<SettingsRoute> {
            SettingsScreen(
                onBack = { navController.navigateUp() },
                onNavigateToLocalePicker = { type ->
                    navController.navigate(LocalePickerRoute(type))
                },
                onNavigateToAutoTranslatePicker = {
                    navController.navigate(LocalePickerRoute(LocalePickerType.AUTO_TRANSLATE_LANGUAGE))
                },
            )
        }

        navGraphBuilder.composable<LocalePickerRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<LocalePickerRoute>()
            LocalePickerScreen(
                type = route.type,
                onBack = { navController.navigateUp() },
            )
        }
    }
}
