package com.co.golapp.attendances.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.co.golapp.attendances.ui.navigation.graphs.AttendanceGraph
import com.co.golapp.attendances.ui.navigation.graphs.GuestGraph
import com.co.golapp.attendances.ui.navigation.graphs.HomeGraph
import com.co.golapp.attendances.ui.screens.attendances.AttendancesScreen
import com.co.golapp.attendances.ui.screens.auth.AuthenticationScreen
import com.co.golapp.attendances.ui.screens.groups.GroupsScreen
import com.co.golapp.attendances.ui.screens.home.HomeScreen

fun NavGraphBuilder.guestGraph(navController: NavController) {
    navigation<GuestGraph.Screens>(startDestination = GuestGraph.Authentication) {
        composable<GuestGraph.Authentication> {
            AuthenticationScreen()
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

fun NavGraphBuilder.groupGraph(navController: NavController) {
    navigation<AttendanceGraph.Screens>(startDestination = AttendanceGraph.Groups) {
        composable<AttendanceGraph.Groups> {
            GroupsScreen()
        }

        composable<AttendanceGraph.Attendances> {
            AttendancesScreen()
        }
    }
}
