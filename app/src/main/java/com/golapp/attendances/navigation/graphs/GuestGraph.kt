package com.golapp.attendances.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.golapp.attendances.feature.auth.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
object Authentication

fun NavController.navigateToAuthentication() =
    navigate(Authentication, navOptions {
        popUpTo(Home) { inclusive = true } // borra Home y el resto
        launchSingleTop = true
    })

fun NavGraphBuilder.authenticationScreens(onDetectLogin: () -> Unit = {}) {
    composable<Authentication> {
        LoginScreen(onDetectLogin = onDetectLogin)
    }
}
