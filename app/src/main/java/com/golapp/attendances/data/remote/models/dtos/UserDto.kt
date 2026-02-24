package com.golapp.attendances.data.remote.models.dtos

import com.golapp.attendances.domain.models.User
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("name") val name: String,
    @SerializedName("school_id") val schoolId: Int,
    @SerializedName("school_logo") val schoolLogo: String,
    @SerializedName("school_name") val schoolName: String,
    @SerializedName("school_slug") val schoolSlug: String
)

fun UserDto.toDomain(): User = User(
    name = name,
    schoolId = schoolId,
    schoolLogo = schoolLogo,
    schoolName = schoolName,
    schoolSlug = schoolSlug
)