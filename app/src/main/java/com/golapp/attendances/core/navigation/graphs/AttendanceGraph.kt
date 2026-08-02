package com.golapp.attendances.core.navigation.graphs

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.golapp.attendances.feature.attendances.AttendancesScreen
import com.golapp.attendances.feature.groups.GroupsScreen
import kotlinx.serialization.Serializable

@Serializable
data object GroupAttendances : NavKey

@Serializable
data object Groups : NavKey

@Serializable
data class Attendances(val classDayId: String) : NavKey

fun EntryProviderScope<NavKey>.groupsScreen(
    onClickClassDay: (String) -> Unit = {},
    onNavigateBackHome: () -> Unit = {},
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    entry<Groups> {
        GroupsScreen(
            onNavigateBackHome = onNavigateBackHome,
            onClickClassDay = onClickClassDay,
            onShowSnackbar = onShowSnackbar
        )
    }

    entry<Attendances> { key ->
        AttendancesScreen(
            classDayId = key.classDayId,
            onShowSnackbar = onShowSnackbar
        )
    }
}
