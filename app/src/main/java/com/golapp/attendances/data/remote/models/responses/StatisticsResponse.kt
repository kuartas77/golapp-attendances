package com.golapp.attendances.data.remote.models.responses

import com.golapp.attendances.data.remote.models.dtos.StatisticsDto
import com.google.gson.annotations.SerializedName

data class StatisticsResponse(
    @SerializedName("data")
    val statistics: List<StatisticsDto>
)
