package com.golapp.attendances.data.mappers

import com.golapp.attendances.data.local.database.entities.AttendanceEntity
import com.golapp.attendances.data.local.database.entities.AttendanceSyncEntity
import com.golapp.attendances.data.local.database.entities.AttendanceWithPlayerEntity
import com.golapp.attendances.data.remote.models.dtos.AttendanceDto
import com.golapp.attendances.data.remote.models.requests.AttendanceRequest
import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.models.AttendanceWithPlayer

fun Attendance.toEntity(idOverride: Long? = id): AttendanceEntity =
    AttendanceEntity(
        id = idOverride,
        attendanceId = attendanceId,
        schoolId = schoolId,
        trainingGroupId = trainingGroupId,
        inscriptionId = inscriptionId,
        year = year,
        month = month,
        column = column,
        value = value,
        playerId = playerId
    )

fun AttendanceWithPlayerEntity.toDomain(): AttendanceWithPlayer =
    AttendanceWithPlayer(
        id = attendance.id,
        attendanceId = attendance.attendanceId,
        schoolId = attendance.schoolId,
        trainingGroupId = attendance.trainingGroupId,
        inscriptionId = attendance.inscriptionId,
        year = attendance.year,
        month = attendance.month,
        column = attendance.column,
        value = attendance.value,
        playerId = attendance.playerId,
        player = player.toDomain()
    )

fun AttendanceDto.toDomain(): Attendance =
    Attendance(
        id = null,
        attendanceId = attendanceId,
        schoolId = schoolId,
        trainingGroupId = trainingGroupId,
        inscriptionId = inscriptionId,
        year = year,
        month = month,
        column = column,
        value = value,
        playerId = playerId
    )

fun AttendanceEntity.toDomain(): Attendance =
    Attendance(
        id = id,
        attendanceId = attendanceId,
        schoolId = schoolId,
        trainingGroupId = trainingGroupId,
        inscriptionId = inscriptionId,
        year = year,
        month = month,
        column = column,
        value = value,
        playerId = playerId
    )

fun AttendanceSyncEntity.toDomain(): AttendanceSync =
    AttendanceSync(
        id = id
    )

fun AttendanceSync.toEntity(): AttendanceSyncEntity =
    AttendanceSyncEntity(
        id = id
    )

fun Attendance.toRequest(): AttendanceRequest = AttendanceRequest(
    attendanceId = attendanceId ?: 0,
    groupId = trainingGroupId,
    inscriptionId = inscriptionId,
    year = year,
    month = month,
    column = column,
    value = value ?: "",
    attendanceDate = null,
    observations = null
)

