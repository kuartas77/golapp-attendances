package com.golapp.attendances.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseLogin(
    @SerialName("access_token") val token: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val type: String,
    @SerialName("expiration") val expires: Long,
    @SerialName("user") val userDto: UserDto?,
    val message: String? = null,
    val code: Int? = null
)
