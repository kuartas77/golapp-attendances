package com.golapp.attendances.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseStatistics(
    @SerialName("data")
    val statistics: List<DtoStatistics>
)
