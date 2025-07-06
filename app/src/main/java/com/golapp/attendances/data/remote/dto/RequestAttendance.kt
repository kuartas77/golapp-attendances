package com.golapp.attendances.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestAttendance(
    @SerialName("id") val attendanceId: Int?,
    @SerialName("group_id") val groupId: Int,
    @SerialName("inscription_id") val inscriptionId: Int,
    @SerialName("year") val year: Int,
    @SerialName("month") val month: Int,
    @SerialName("column") val column: String,
    @SerialName("value") val value: String,
    @SerialName("attendance_date") val attendanceDate: String?,
    @SerialName("observations") val observations: String?,
)
