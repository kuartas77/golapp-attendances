package com.golapp.attendances.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseGroup(
    @field:Json(name = "data")
    val group: DtoGroup
)
