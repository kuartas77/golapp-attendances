package com.co.golapp.attendances.ui.navigation.graphs

import kotlinx.serialization.Serializable

sealed class GuestGraph {
    @Serializable
    object Screens

    @Serializable
    object Authentication
}
