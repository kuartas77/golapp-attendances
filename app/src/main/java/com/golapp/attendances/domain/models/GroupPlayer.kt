package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupPlayer(
    val player: Player,
    val inscriptionId: Long
): Parcelable
