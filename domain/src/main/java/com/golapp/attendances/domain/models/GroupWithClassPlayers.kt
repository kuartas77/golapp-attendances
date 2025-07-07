package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupWithClassPlayers(
    val group: Group,
    val players: List<Player>,
    val classDays: List<ClassDay>
) : Parcelable
