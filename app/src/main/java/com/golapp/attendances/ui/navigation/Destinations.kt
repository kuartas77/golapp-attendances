package com.golapp.attendances.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.golapp.attendances.R
import com.golapp.attendances.ui.navigation.graphs.GroupAttendances
import com.golapp.attendances.ui.navigation.graphs.Groups
import com.golapp.attendances.ui.navigation.graphs.Home
import com.golapp.attendances.ui.navigation.graphs.Settings
import kotlin.reflect.KClass


enum class Destinations(
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
    @param:DrawableRes val icon: Int,
    @param:StringRes val label: Int
) {
    HOME(
        route = Home::class,
        icon = R.drawable.ic_home,
        label = R.string.home
    ),
    GROUPS(
        route = Groups::class,
        icon = R.drawable.ic_team,
        label = R.string.groups,
        baseRoute = GroupAttendances::class
    ),
    SETTINGS(
        route = Settings::class,
        icon = R.drawable.ic_settings,
        label = R.string.settings
    )
}
