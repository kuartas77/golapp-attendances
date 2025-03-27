package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClassDay(
    override val classDayId: String,
    override val date: Int,
    override val day: String,
    override val month: Int,
    override val monthName: String,
    override val column: String,
    override val groupId: Int,
    override val schoolId: Int
) : IClassDay, Parcelable
