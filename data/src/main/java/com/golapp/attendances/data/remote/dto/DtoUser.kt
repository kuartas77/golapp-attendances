package com.golapp.attendances.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("name") val name: String,
    @SerialName("school_id") val schoolId: Int,
    @SerialName("school_logo") val schoolLogo: String,
    @SerialName("school_name") val schoolName: String,
    @SerialName("school_slug") val schoolSlug: String
)
