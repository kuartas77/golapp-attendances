package com.golapp.attendances.ui.navigation

import com.golapp.attendances.R
import com.golapp.attendances.ui.navigation.graphs.AttendanceGraph
import com.golapp.attendances.ui.navigation.graphs.HomeGraph


sealed class Destinations<T>(val route: String, val icon: Int, val label: Int) {
    data object Home : Destinations<HomeGraph.Home>(
        HomeGraph.Home::class.qualifiedName.toString(),
        R.drawable.ic_home,
        R.string.home
    )

    data object Groups : Destinations<AttendanceGraph.Groups>(
        AttendanceGraph.Groups::class.qualifiedName.toString(),
        R.drawable.ic_team,
        R.string.groups
    )
}
