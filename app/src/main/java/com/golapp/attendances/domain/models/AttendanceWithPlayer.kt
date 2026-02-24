package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AttendanceWithPlayer(
    val id: Long?,
    val attendanceId: Int?,
    val schoolId: Int,
    val trainingGroupId: Int,
    val inscriptionId: Int,
    val year: Int,
    val month: Int,
    val column: String,
    var value: String?,
    val playerId: Int,
    val player: Player
) : Parcelable