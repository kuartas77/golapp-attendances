package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClassDay(
     val classDayId: String,
     val date: Int,
     val day: String,
     val month: Int,
     val monthName: String,
     val column: String,
     val groupId: Int,
     val schoolId: Int
) : Parcelable
