package com.golapp.attendances.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DtoAttendance(
    @SerialName("column")
    val column: String,
    @SerialName("id")
    val attendanceId: Int,
    @SerialName("inscription_id")
    val inscriptionId: Int,
    @SerialName("month")
    val month: Int,
    @SerialName("player_id")
    val playerId: Int,
    @SerialName("school_id")
    val schoolId: Int,
    @SerialName("training_group_id")
    val trainingGroupId: Int,
    @SerialName("value")
    val value: String?,
    @SerialName("year")
    val year: Int
)
