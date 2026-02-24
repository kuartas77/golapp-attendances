package com.golapp.attendances.data.remote.models.responses

import com.golapp.attendances.data.remote.models.dtos.UserDto
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token") val token: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val type: String,
    @SerializedName("expiration") val expires: Long,
    @SerializedName("user") val userDto: UserDto?,
    val message: String? = null,
    val code: Int? = null
)
