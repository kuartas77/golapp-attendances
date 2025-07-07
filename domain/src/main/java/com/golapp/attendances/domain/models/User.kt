package com.golapp.attendances.domain.models

data class User(
    val name: String,
    val schoolId: Int,
    val schoolName: String,
    val schoolSlug: String,
    val schoolLogo: String
)
