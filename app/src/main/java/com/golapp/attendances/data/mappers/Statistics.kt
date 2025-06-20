package com.golapp.attendances.data.mappers

import com.golapp.attendances.data.local.models.StatisticsEntity
import com.golapp.attendances.data.remote.dto.DtoStatistics
import com.golapp.attendances.data.remote.dto.ResponseStatistics
import com.golapp.attendances.domain.models.Statistics

fun DtoStatistics.asEntity(): StatisticsEntity = StatisticsEntity(
    attendancesNoTaken,
    attendancesTaken,
    attendancesTotal,
    avg,
    fullGroup,
    groupId
)

@JvmName("ListStatisticsDtoAsEntity")
fun List<DtoStatistics>.asEntity(): List<StatisticsEntity> = map { it.asEntity() }

fun ResponseStatistics.asEntity(): List<StatisticsEntity> = statistics.asEntity()

fun StatisticsEntity.asDomain(): Statistics = Statistics(
    attendancesNoTaken,
    attendancesTaken,
    attendancesTotal,
    avg,
    fullGroup,
    groupId
)



