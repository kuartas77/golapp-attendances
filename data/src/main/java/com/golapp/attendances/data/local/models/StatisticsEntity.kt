package com.golapp.attendances.data.local.models

data class StatisticsEntity(
    val attendancesNoTaken: Int,
    val attendancesTaken: Int,
    val attendancesTotal: Int,
    val avg: String,
    val fullGroup: String,
    val groupId: Int
)
