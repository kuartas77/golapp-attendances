package com.golapp.attendances.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.golapp.attendances.feature.main.GolAppState
import com.golapp.attendances.navigation.graphs.Authentication
import com.golapp.attendances.navigation.graphs.authenticationScreens
import com.golapp.attendances.navigation.graphs.groupsScreen
import com.golapp.attendances.navigation.graphs.homeScreen
import com.golapp.attendances.navigation.graphs.Home
import com.golapp.attendances.navigation.graphs.navigateToAuthentication
import com.golapp.attendances.navigation.graphs.navigateToGroups
import com.golapp.attendances.navigation.graphs.navigateToHome
import com.golapp.attendances.navigation.graphs.settingScreen

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

        homeScreen(onLogout = {
            mainViewModel.logout()
            navController.navigateToAuthentication()
        })

        settingScreen(onLogout = {
            mainViewModel.logout()
            navController.navigateToAuthentication()
        })

        groupsScreen(
            onNavigateBackHome = {
                navController.navigateToHome(
                    navOptions {
                        popUpTo(Home) {
                            inclusive = false
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                )
            },
            onClickClassDay = {
                navController.navigateToGroups(it)
            },
            onShowSnackbar = onShowSnackbar
        )
    }

}
