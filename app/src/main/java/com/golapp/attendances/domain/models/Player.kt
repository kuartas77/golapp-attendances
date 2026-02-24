package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    val playerId: Int,
    val groupId: Int,
    val uniqueCode: String,
    val names: String,
    val lastNames: String,
    val category: String,
    val fullNames: String,
    val photoUrl: String,
    val inscriptionId: Int
) : Parcelable
