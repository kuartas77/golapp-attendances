package com.golapp.attendances.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.golapp.attendances.feature.auth.LoginScreen
import kotlinx.serialization.Serializable

@Serializable
object Authentication

fun NavController.navigateToAuthentication(navOptions: NavOptions? = null) =
    navigate(Authentication, navOptions)

fun NavGraphBuilder.authenticationScreens(onDetectLogin: () -> Unit = {}) {
    composable<Authentication> {
        LoginScreen(onDetectLogin = onDetectLogin)
    }
}
