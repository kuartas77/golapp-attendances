package com.golapp.attendances.data.remote.models.requests

import com.google.gson.annotations.SerializedName

data class AttendanceRequest(
    @SerializedName("id") val attendanceId: Int?,
    @SerializedName("group_id") val groupId: Int,
    @SerializedName("inscription_id") val inscriptionId: Int,
    @SerializedName("year") val year: Int,
    @SerializedName("month") val month: Int,
    @SerializedName("column") val column: String,
    @SerializedName("value") val value: String,
    @SerializedName("attendance_date") val attendanceDate: String?,
    @SerializedName("observations") val observations: String?,
)
