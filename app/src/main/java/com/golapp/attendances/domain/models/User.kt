package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val name: String,
    val schoolId: Int,
    val schoolName: String,
    val schoolSlug: String,
    val schoolLogo: String
) : Parcelable