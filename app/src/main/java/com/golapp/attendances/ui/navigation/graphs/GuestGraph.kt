package com.golapp.attendances.ui.navigation.graphs

import kotlinx.serialization.Serializable

sealed class GuestGraph {
    @Serializable
    object Guest

    @Serializable
    object Authentication
}
