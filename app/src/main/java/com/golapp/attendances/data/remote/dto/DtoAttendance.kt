package com.golapp.attendances.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DtoAttendance(
    @field:Json(name = "column")
    val column: String,
    @field:Json(name = "id")
    val attendanceId: Int,
    @field:Json(name = "inscription_id")
    val inscriptionId: Int,
    @field:Json(name = "month")
    val month: Int,
    @field:Json(name = "player_id")
    val playerId: Int,
    @field:Json(name = "school_id")
    val schoolId: Int,
    @field:Json(name = "training_group_id")
    val trainingGroupId: Int,
    @field:Json(name = "value")
    val value: String?,
    @field:Json(name = "year")
    val year: Int
)
