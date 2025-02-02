package com.golapp.attendances.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestAttendance(
    @field:Json(name = "training_group_id") val groupId: Int,
    @field:Json(name = "month") val month: Int,
    @field:Json(name = "column") val column: String
)
