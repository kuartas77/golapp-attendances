package com.golapp.attendances.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DtoClassDay(
    @SerialName("column")
    val column: String,
    @SerialName("date")
    val date: Int,
    @SerialName("day")
    val day: String,
    @SerialName("group_id")
    val groupId: Int,
    @SerialName("id")
    val id: String,
    @SerialName("month")
    val month: Int,
    @SerialName("month_name")
    val monthName: String,
    @SerialName("school_id")
    val schoolId: Int,
    @SerialName("year")
    val year: Int? = null
)
