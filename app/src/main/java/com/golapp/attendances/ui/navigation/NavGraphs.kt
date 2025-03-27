package com.golapp.attendances.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.golapp.attendances.common.ui.GolAppState
import com.golapp.attendances.ui.navigation.graphs.AttendanceGraph
import com.golapp.attendances.ui.navigation.graphs.GuestGraph
import com.golapp.attendances.ui.navigation.graphs.HomeGraph
import com.golapp.attendances.ui.screens.MainViewModel
import com.golapp.attendances.ui.screens.attendances.presentation.AttendancesScreen
import com.golapp.attendances.ui.screens.auth.presentation.AuthenticationScreen
import com.golapp.attendances.ui.screens.groups.presentation.GroupsScreen
import com.golapp.attendances.ui.screens.home.HomeScreen
import com.golapp.attendances.ui.screens.settings.presentation.SettingsScreen
import kotlinx.coroutines.delay

fun NavGraphBuilder.guestGraph(appState: GolAppState) {
    val navController = appState.navController
    val mainViewModel = appState.mainViewModel
    navigation<GuestGraph.Screens>(startDestination = GuestGraph.Authentication) {
        composable<GuestGraph.Authentication> {
            AuthenticationScreen {
                navController.navigate(HomeGraph.Screens) {
                    navController.popBackStack()
                }
            }
        }
    }
}

fun NavGraphBuilder.homeGraph(appState: GolAppState) {
    val navController = appState.navController
    val mainViewModel = appState.mainViewModel
    navigation<HomeGraph.Screens>(startDestination = HomeGraph.Home) {
        composable<HomeGraph.Home> {
            HomeScreen()
        }

        composable<HomeGraph.Settings> {
            SettingsScreen {
                mainViewModel.logout()
                navController.navigate(GuestGraph.Screens) {
                    popUpTo(0){
                        inclusive = true
                    }
                }
            }
        }
    }
}

fun NavGraphBuilder.attendanceGraph(appState: GolAppState) {
    val navController = appState.navController
    val mainViewModel = appState.mainViewModel
    navigation<AttendanceGraph.Screens>(startDestination = AttendanceGraph.Groups) {
        composable<AttendanceGraph.Groups> {
            GroupsScreen(
                onClickClassDay = { classDayId ->
                    navController.navigate(AttendanceGraph.Attendances(classDayId))
                }
            )
        }

        composable<AttendanceGraph.Attendances> {
            AttendancesScreen()
        }
    }
}
