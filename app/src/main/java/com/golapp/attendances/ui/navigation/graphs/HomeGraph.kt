package com.golapp.attendances.ui.navigation.graphs

import kotlinx.serialization.Serializable

sealed class HomeGraph {
    @Serializable
    object Screens

    @Serializable
    object Home
}

sealed class UserGraph {
    @Serializable
    object Account

    @Serializable
    object Settings
}


