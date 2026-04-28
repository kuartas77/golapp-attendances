package com.golapp.attendances.navigation.graphs

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.golapp.attendances.feature.auth.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
data object Authentication : NavKey

fun EntryProviderScope<NavKey>.authenticationScreens(onDetectLogin: () -> Unit = {}) {
    entry<Authentication> {
        LoginScreen(onDetectLogin = onDetectLogin)
    }
}
