package com.golapp.attendances.ui.navigation

import com.golapp.attendances.R
import com.golapp.attendances.ui.navigation.graphs.AttendanceGraph
import com.golapp.attendances.ui.navigation.graphs.HomeGraph
import com.golapp.attendances.ui.navigation.graphs.UserGraph


sealed class Destinations<T: Any>(val route: T, val icon: Int, val label: Int) {
    data object Home : Destinations<HomeGraph.Home>(
        HomeGraph.Home,
        R.drawable.ic_home,
        R.string.home
    )

    data object Groups : Destinations<AttendanceGraph.Groups>(
        AttendanceGraph.Groups,
        R.drawable.ic_team,
        R.string.groups
    )
    data object Settings : Destinations<UserGraph.Settings>(
        UserGraph.Settings,
        R.drawable.ic_settings,
        R.string.settings
    )
}
