package com.golapp.attendances.data.remote.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseStatistics(
    @Json(name = "data")
    val statistics: List<DtoStatistics>
)
