package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    val id: Int,
    val name: String,
    val days: String,
    val explodeSchedules: String,
    val fullScheduleGroup: String,
    val fullGroup: String,
    val playerCount: Int
) : Parcelable
