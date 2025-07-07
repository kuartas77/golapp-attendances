package com.golapp.attendances.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DtoStatistics(
    @SerialName("attendances_no_taken")
    val attendancesNoTaken: Int,
    @SerialName("attendances_taken")
    val attendancesTaken: Int,
    @SerialName("attendances_total")
    val attendancesTotal: Int,
    @SerialName("avg")
    val avg: String,
    @SerialName("full_group")
    val fullGroup: String,
    @SerialName("group_id")
    val groupId: Int
)
