package com.golapp.attendances.data.mappers

import com.golapp.attendances.data.local.models.ClassDayEntity
import com.golapp.attendances.data.remote.dto.DtoClassDay
import com.golapp.attendances.domain.models.ClassDay

fun DtoClassDay.asEntity(): ClassDayEntity =
    ClassDayEntity(
        classDayId = id,
        date = date,
        day = day,
        month = month,
        monthName = monthName,
        column = column,
        groupId = groupId,
        schoolId = schoolId
    )

@JvmName("ListClassDayDtoAsEntity")
fun List<DtoClassDay>.asEntity(): List<ClassDayEntity> = map { it.asEntity() }

fun ClassDay.asEntity(): ClassDayEntity = ClassDayEntity(
    classDayId = classDayId,
    date = date,
    day = day,
    month = month,
    monthName = monthName,
    column = column,
    groupId = groupId,
    schoolId = schoolId
)

@JvmName("ListClassDayAsEntity")
fun List<ClassDay>.asEntity(): List<ClassDayEntity> = map { it.asEntity() }

fun ClassDayEntity.asDomain(): ClassDay = ClassDay(
    classDayId = classDayId,
    date = date,
    day = day,
    month = month,
    monthName = monthName,
    column = column,
    groupId = groupId,
    schoolId = schoolId
)

@JvmName("ListClassDayEntityAsDomain")
fun List<ClassDayEntity>.asDomain(): List<ClassDay> = map { it.asDomain() }
