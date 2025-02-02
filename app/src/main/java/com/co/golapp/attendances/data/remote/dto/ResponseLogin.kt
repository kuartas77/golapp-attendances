package com.co.golapp.attendances.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseLogin(
    @field:Json(name = "access_token") val token: String,
    @field:Json(name = "token_type") val type: String,
    @field:Json(name = "expiration") val expires: Long,
    val message: String? = null,
    val code: Int? = null
)
