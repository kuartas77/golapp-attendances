package com.co.golapp.attendances.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseAttendances(
    @field:Json(name = "data")
    val attendances: List<DtoAttendance>
)
