package com.golapp.attendances.ui.navigation.graphs

import kotlinx.serialization.Serializable

sealed class AttendanceGraph {
    @Serializable
    object Attendances

    @Serializable
    object Groups

    @Serializable
    data class GroupsAttendances(val classDayId: String)
}
