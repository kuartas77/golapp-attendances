package com.golapp.attendances.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponseDto(
    @field:Json(name = "data") val `data`: DataDto
)

@JsonClass(generateAdapter = true)
data class DataDto(
    @field:Json(name = "user") val userDto: UserDto
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @field:Json(name = "email") val email: String,
    @field:Json(name = "id") val userId: Int,
    @field:Json(name = "name") val name: String,
    @field:Json(name = "school_id") val schoolId: Int,
    @field:Json(name = "school_logo") val schoolLogo: String,
    @field:Json(name = "school_name") val schoolName: String,
    @field:Json(name = "school_slug") val schoolSlug: String
)
