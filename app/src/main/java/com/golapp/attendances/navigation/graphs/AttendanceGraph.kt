package com.golapp.attendances.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.golapp.attendances.feature.attendances.AttendancesScreen
import com.golapp.attendances.feature.groups.GroupsScreen
import kotlinx.serialization.Serializable

@Serializable
object GroupAttendances

@Serializable
object Groups

@Serializable
data class Attendances(val classDayId: String)

fun NavController.navigateToGroups(navOptions: NavOptions) =
    navigate(Groups, navOptions)

fun NavController.navigateToGroups(
    classDayId: String,
    navOptions: NavOptions? = null
) = navigate(Attendances(classDayId), navOptions ?: androidx.navigation.navOptions {
    popUpTo(Groups) {
        inclusive = false
        saveState = false
    }
    launchSingleTop = true
    restoreState = false
})

fun NavGraphBuilder.groupsScreen(
    onClickClassDay: (String) -> Unit = {},
    onNavigateBackHome: () -> Unit = {},
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    navigation<GroupAttendances>(startDestination = Groups) {

        composable<Groups> {
            GroupsScreen(
                onNavigateBackHome = onNavigateBackHome,
                onClickClassDay = onClickClassDay,
                onShowSnackbar = onShowSnackbar
            )
        }

        composable<Attendances> {
            // Si luego Attendances necesita UiEffect/snackbar, ya lo tienes listo:
            AttendancesScreen(
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}
