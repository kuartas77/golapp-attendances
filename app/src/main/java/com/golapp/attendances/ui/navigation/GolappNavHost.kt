package com.golapp.attendances.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.golapp.attendances.ui.GolAppState
import com.golapp.attendances.ui.navigation.graphs.Authentication
import com.golapp.attendances.ui.navigation.graphs.Groups
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
                navController.navigateToHome(navOptions {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                })
            }
        )

        homeScreen()

        settingScreen(onLogout = {
            mainViewModel.logout()
            navController.navigateToAuthentication(navOptions {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            })
        })

        groupsScreen(
            onClickClassDay = {
                navController.navigateToGroups(it, navOptions {
                    popUpTo(Groups) {
                        inclusive = false
                    }
                })
            }
        )
    }

}
