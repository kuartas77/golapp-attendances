package com.golapp.attendances.data.remote.models.dtos

import com.golapp.attendances.domain.models.ClassDay
import com.google.gson.annotations.SerializedName

data class ClassDayDto(
    @SerializedName("column")
    val column: String,
    @SerializedName("date")
    val date: Int,
    @SerializedName("day")
    val day: String,
    @SerializedName("group_id")
    val groupId: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("month")
    val month: Int,
    @SerializedName("month_name")
    val monthName: String,
    @SerializedName("school_id")
    val schoolId: Int,
    @SerializedName("year")
    val year: Int? = null
)

fun ClassDayDto.toDomain(): ClassDay =
    ClassDay(
        column = column,
        date = date,
        day = day,
        month = month,
        monthName = monthName,
        groupId = groupId,
        schoolId = schoolId,
        classDayId = id
    )