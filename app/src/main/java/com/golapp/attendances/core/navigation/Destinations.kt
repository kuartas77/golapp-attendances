package com.golapp.attendances.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import com.golapp.attendances.R
import com.golapp.attendances.core.navigation.graphs.Attendances
import com.golapp.attendances.core.navigation.graphs.GroupAttendances
import com.golapp.attendances.core.navigation.graphs.Groups
import com.golapp.attendances.core.navigation.graphs.Home
import com.golapp.attendances.core.navigation.graphs.Settings
import kotlin.reflect.KClass


enum class Destinations(
    val route: NavKey,
    val selectedRoutes: Set<KClass<out NavKey>> = setOf(route::class),
    @param:DrawableRes val icon: Int,
    @param:StringRes val label: Int
) {
    HOME(
        route = Home,
        icon = R.drawable.ic_home,
        label = R.string.home
    ),
    GROUPS(
        route = Groups,
        icon = R.drawable.ic_team,
        label = R.string.groups,
        selectedRoutes = setOf(GroupAttendances::class, Groups::class, Attendances::class)
    ),
    SETTINGS(
        route = Settings,
        icon = R.drawable.ic_settings,
        label = R.string.settings
    );

    fun isSelected(currentDestination: NavKey?): Boolean =
        currentDestination?.let { it::class in selectedRoutes } == true
}
