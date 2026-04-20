package com.golapp.attendances.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.golapp.attendances.feature.home.HomeScreen
import com.golapp.attendances.feature.settings.SettingsScreen
import kotlinx.serialization.Serializable


@Serializable
object Home

@Serializable
object Settings

fun NavController.navigateToHome(navOptions: NavOptions? = null) = navigate(Home, navOptions)

fun NavGraphBuilder.homeScreen(onLogout: () -> Unit = {}) {
    composable<Home> {
        HomeScreen(onLogout = onLogout)
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
