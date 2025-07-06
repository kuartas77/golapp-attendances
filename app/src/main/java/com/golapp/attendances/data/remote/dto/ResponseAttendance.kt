package com.golapp.attendances.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseAttendances(
    @SerialName("data")
    val attendances: List<DtoAttendance>
)
