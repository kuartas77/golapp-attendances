package com.golapp.attendances.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.golapp.attendances.ui.navigation.graphs.AttendanceGraph
import com.golapp.attendances.ui.navigation.graphs.GuestGraph
import com.golapp.attendances.ui.navigation.graphs.HomeGraph
import com.golapp.attendances.ui.screens.attendances.AttendancesScreen
import com.golapp.attendances.ui.screens.auth.AuthenticationScreen
import com.golapp.attendances.ui.screens.groups.presentation.GroupsScreen
import com.golapp.attendances.ui.screens.home.HomeScreen

fun NavGraphBuilder.guestGraph(navController: NavController) {
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

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<HomeGraph.Screens>(startDestination = HomeGraph.Home) {
        composable<HomeGraph.Home> {
            HomeScreen()
        }
        composable<HomeGraph.Profile> {
            TODO("screen not created yet")
        }
        composable<HomeGraph.Settings> {
            TODO("screen not created yet")
        }
    }
}

fun NavGraphBuilder.attendanceGraph(navController: NavController) {
    navigation<AttendanceGraph.Screens>(startDestination = AttendanceGraph.Groups) {
        composable<AttendanceGraph.Groups> {
            GroupsScreen()
        }

        composable<AttendanceGraph.Attendances> {
            AttendancesScreen()
        }
    }
}
