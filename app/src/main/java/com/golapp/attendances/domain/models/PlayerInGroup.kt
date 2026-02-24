package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayerInGroup(val playerId: Int, val inscriptionId: Int): Parcelable
