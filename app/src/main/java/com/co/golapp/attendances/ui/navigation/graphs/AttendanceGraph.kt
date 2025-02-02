package com.co.golapp.attendances.ui.navigation.graphs

import kotlinx.serialization.Serializable

sealed class AttendanceGraph {
    @Serializable
    object Screens

    @Serializable
    object Groups

    @Serializable
    data class Attendances(val classDayId: String)
}
