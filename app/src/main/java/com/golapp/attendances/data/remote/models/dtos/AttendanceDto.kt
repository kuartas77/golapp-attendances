package com.golapp.attendances.data.remote.models.dtos

import com.google.gson.annotations.SerializedName

data class AttendanceDto(
    @SerializedName("column")
    val column: String,
    @SerializedName("id")
    val attendanceId: Int,
    @SerializedName("inscription_id")
    val inscriptionId: Int,
    @SerializedName("month")
    val month: Int,
    @SerializedName("player_id")
    val playerId: Int,
    @SerializedName("school_id")
    val schoolId: Int,
    @SerializedName("training_group_id")
    val trainingGroupId: Int,
    @SerializedName("value")
    val value: String?,
    @SerializedName("year")
    val year: Int
)