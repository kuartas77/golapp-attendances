package com.golapp.attendances.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseGroups(
    @SerialName("data")
    val groups: List<DtoGroup>
)
