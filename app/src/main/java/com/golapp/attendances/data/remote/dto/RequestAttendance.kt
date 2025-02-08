package com.golapp.attendances.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestAttendance(
    @field:Json(name = "inscription_id") val attendanceId: Int,
    @field:Json(name = "group_id") val groupId: Int,
    @field:Json(name = "year") val year: Int,
    @field:Json(name = "month") val month: Int,
    @field:Json(name = "column") val column: String,
    @field:Json(name = "value") val value: String,
    @field:Json(name = "id") val id: Int?,
    @field:Json(name = "attendance_date") val attendanceDate: String?,
    @field:Json(name = "observations") val observations: String?,
)
