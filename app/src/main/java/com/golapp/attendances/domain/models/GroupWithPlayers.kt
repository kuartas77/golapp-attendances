package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupWithPlayers(
    val group: Group,
    val players: List<Player>
) : Parcelable
