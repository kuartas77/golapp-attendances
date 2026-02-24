package com.golapp.attendances.data.remote.models.responses

import com.golapp.attendances.data.remote.models.dtos.AttendanceDto
import com.google.gson.annotations.SerializedName

data class AttendancesResponse(
    @SerializedName("data")
    val attendances: List<AttendanceDto>
)
