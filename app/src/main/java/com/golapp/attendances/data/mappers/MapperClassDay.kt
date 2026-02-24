package com.golapp.attendances.data.mappers

import com.golapp.attendances.data.local.database.entities.ClassDayEntity
import com.golapp.attendances.domain.models.ClassDay

fun ClassDayEntity.toDomain(): ClassDay =
    ClassDay(
        classDayId = classDayId,
        date = date,
        day = day,
        month = month,
        monthName = monthName,
        column = column,
        groupId = groupId,
        schoolId = schoolId
    )

fun ClassDay.toEntity(): ClassDayEntity =
    ClassDayEntity(
        classDayId = classDayId,
        date = date,
        day = day,
        month = month,
        monthName = monthName,
        column = column,
        groupId = groupId,
        schoolId = schoolId
    )
