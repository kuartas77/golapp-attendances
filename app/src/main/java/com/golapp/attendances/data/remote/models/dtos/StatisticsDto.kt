package com.golapp.attendances.data.remote.models.dtos

import com.golapp.attendances.domain.models.Statistics
import com.google.gson.annotations.SerializedName

data class StatisticsDto(
    @SerializedName("attendances_no_taken")
    val attendancesNoTaken: Int,
    @SerializedName("attendances_taken")
    val attendancesTaken: Int,
    @SerializedName("attendances_total")
    val attendancesTotal: Int,
    @SerializedName("avg")
    val avg: String,
    @SerializedName("full_group")
    val fullGroup: String,
    @SerializedName("group_id")
    val groupId: Int
)

fun StatisticsDto.toDomain(): Statistics =
    Statistics(
        attendancesNoTaken = attendancesNoTaken,
        attendancesTaken = attendancesTaken,
        attendancesTotal = attendancesTotal,
        avg = avg,
        fullGroup = fullGroup,
        groupId = groupId
    )
