package com.golapp.attendances.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.golapp.attendances.ui.GolAppState
import com.golapp.attendances.ui.navigation.graphs.Authentication
import com.golapp.attendances.ui.navigation.graphs.Home
import com.golapp.attendances.ui.navigation.graphs.authenticationScreens
import com.golapp.attendances.ui.navigation.graphs.groupsScreen
import com.golapp.attendances.ui.navigation.graphs.homeScreen
import com.golapp.attendances.ui.navigation.graphs.navigateToAuthentication
import com.golapp.attendances.ui.navigation.graphs.navigateToGroups
import com.golapp.attendances.ui.navigation.graphs.navigateToHome
import com.golapp.attendances.ui.navigation.graphs.settingScreen

@Composable
fun GolappNavHost(
    appState: GolAppState,
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val navController = appState.navController
    val mainViewModel = appState.mainViewModel
    NavHost(
        navController = navController,
        startDestination = Authentication,
        modifier = modifier
    ) {

        authenticationScreens(
            onDetectLogin = {
                navController.navigateToHome(
                    navOptions {
                        popUpTo(Authentication) { inclusive = true } // elimina Auth del stack
                        launchSingleTop = true
                    }
                )
            }
        )

        homeScreen()

        settingScreen(onLogout = {
            mainViewModel.logout()
            navController.navigateToAuthentication(
                navOptions {
                    popUpTo(Home) { inclusive = true } // borra Home y el resto
                    launchSingleTop = true
                }
            )
        })

        groupsScreen(
            onClickClassDay = {
                navController.navigateToGroups(it)
            },
            onShowSnackbar = onShowSnackbar
        )
    }

}
