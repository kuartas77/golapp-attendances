package com.co.golapp.attendances.ui.navigation.graphs

import kotlinx.serialization.Serializable

sealed class HomeGraph {
    @Serializable
    object Screens

    @Serializable
    object Home

    @Serializable
    object Profile

    @Serializable
    object Settings

}
