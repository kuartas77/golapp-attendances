package com.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Statistics(
    val attendancesNoTaken: Int,
    val attendancesTaken: Int,
    val attendancesTotal: Int,
    val avg: String,
    val fullGroup: String,
    val groupId: Int
) : Parcelable