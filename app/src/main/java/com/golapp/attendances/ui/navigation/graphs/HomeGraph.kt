package com.golapp.attendances.ui.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.golapp.attendances.ui.screens.home.HomeScreen
import com.golapp.attendances.ui.screens.settings.presentation.SettingsScreen
import kotlinx.serialization.Serializable


@Serializable
object Home

@Serializable
object Settings

fun NavController.navigateToHome(navOptions: NavOptions? = null) = navigate(Home, navOptions)

fun NavGraphBuilder.homeScreen() {
    composable<Home> {
        HomeScreen()
    }
}

fun NavController.navigateToSettings(navOptions: NavOptions? = null) =
    navigate(Settings, navOptions)

fun NavGraphBuilder.settingScreen(
    onLogout: () -> Unit
) {
    composable<Settings> {
        SettingsScreen(onLogout = onLogout)
    }
}
