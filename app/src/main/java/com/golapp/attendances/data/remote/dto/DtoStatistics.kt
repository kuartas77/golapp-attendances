package com.golapp.attendances.data.remote.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DtoStatistics(
    @Json(name = "attendances_no_taken")
    val attendancesNoTaken: Int,
    @Json(name = "attendances_taken")
    val attendancesTaken: Int,
    @Json(name = "attendances_total")
    val attendancesTotal: Int,
    @Json(name = "avg")
    val avg: String,
    @Json(name = "full_group")
    val fullGroup: String,
    @Json(name = "group_id")
    val groupId: Int
)
