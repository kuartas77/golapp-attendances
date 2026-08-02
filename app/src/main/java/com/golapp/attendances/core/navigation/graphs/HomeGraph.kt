package com.golapp.attendances.core.navigation.graphs

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.golapp.attendances.feature.home.HomeScreen
import com.golapp.attendances.feature.settings.SettingsScreen
import kotlinx.serialization.Serializable


@Serializable
data object Home : NavKey

@Serializable
data object Settings : NavKey

fun EntryProviderScope<NavKey>.homeScreen(onLogout: () -> Unit = {}) {
    entry<Home> {
        HomeScreen(onLogout = onLogout)
    }
}

fun EntryProviderScope<NavKey>.settingScreen(
    onLogout: () -> Unit
) {
    entry<Settings> {
        SettingsScreen(onLogout = onLogout)
    }
}
