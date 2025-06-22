package com.golapp.attendances.ui.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.golapp.attendances.ui.screens.attendances.presentation.AttendancesScreen
import com.golapp.attendances.ui.screens.groups.presentation.GroupsScreen
import kotlinx.serialization.Serializable


@Serializable
object GroupAttendances

@Serializable
object Groups

@Serializable
data class Attendances(val classDayId: String)

fun NavController.navigateToGroups(navOptions: NavOptions) = navigate(Groups, navOptions)

fun NavController.navigateToGroups(
    classDayId: String,
    navOptions: NavOptionsBuilder.() -> Unit = {}
) = navigate(Attendances(classDayId), navOptions)

fun NavGraphBuilder.groupsScreen(
    onClickClassDay: (String) -> Unit = {}
) {
    navigation<GroupAttendances>(startDestination = Groups) {
        composable<Groups> {
            GroupsScreen(onClickClassDay = onClickClassDay)
        }
        composable<Attendances> {
            AttendancesScreen()
        }
    }

}
