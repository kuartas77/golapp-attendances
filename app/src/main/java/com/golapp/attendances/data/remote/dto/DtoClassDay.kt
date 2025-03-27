package com.golapp.attendances.data.remote.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DtoClassDay(
    @field:Json(name = "column")
    val column: String,
    @field:Json(name = "date")
    val date: Int,
    @field:Json(name = "day")
    val day: String,
    @field:Json(name = "group_id")
    val groupId: Int,
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "month")
    val month: Int,
    @field:Json(name = "month_name")
    val monthName: String,
    @field:Json(name = "school_id")
    val schoolId: Int
)
