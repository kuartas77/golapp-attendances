package com.golapp.attendances.data.mappers

import com.golapp.attendances.data.local.models.AttendanceEntity
import com.golapp.attendances.data.local.models.AttendanceSyncEntity
import com.golapp.attendances.data.local.models.AttendanceWithPlayerEntity
import com.golapp.attendances.data.remote.dto.DtoAttendance
import com.golapp.attendances.data.remote.dto.ResponseAttendances
import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.models.AttendanceWithPlayer

fun DtoAttendance.asEntity(): AttendanceEntity = AttendanceEntity(
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

fun DtoAttendance.asDomain(): Attendance = Attendance(
    attendanceId = attendanceId,
    schoolId = schoolId,
    trainingGroupId = trainingGroupId,
    inscriptionId = inscriptionId,
    year = year,
    month = month,
    column = column,
    value = value,
    playerId = playerId,
    id = 0
)

fun AttendanceEntity.asDomain(): Attendance = Attendance(
    attendanceId = attendanceId,
    schoolId = schoolId,
    trainingGroupId = trainingGroupId,
    inscriptionId = inscriptionId,
    year = year,
    month = month,
    column = column,
    value = value,
    playerId = playerId,
    id = 0
)

@JvmName("ListAttendanceEntityAsDomain")
fun List<AttendanceEntity>.asDomain(): List<Attendance> = map { it.asDomain() }

fun ResponseAttendances.asEntity(): List<AttendanceEntity> = attendances.asEntity()

@JvmName("ListAttendanceDtoAsEntity")
fun List<DtoAttendance>.asEntity(): List<AttendanceEntity> = map { it.asEntity() }

@JvmName("ListAttendanceDtoAsDomain")
fun List<DtoAttendance>.asDomain(): List<Attendance> = map { it.asDomain() }

fun Attendance.asEntity(): AttendanceEntity = AttendanceEntity(
    attendanceId = attendanceId,
    schoolId = schoolId,
    trainingGroupId = trainingGroupId,
    inscriptionId = inscriptionId,
    year = year,
    month = month,
    column = column,
    value = value,
    playerId = playerId,
    id = id
)

@JvmName("ListAttendanceAsEntity")
fun List<Attendance>.asEntity(): List<AttendanceEntity> = map { it.asEntity() }

fun AttendanceWithPlayerEntity.asDomain(): AttendanceWithPlayer = AttendanceWithPlayer(
    attendanceId = attendance.attendanceId,
    schoolId = attendance.schoolId,
    trainingGroupId = attendance.trainingGroupId,
    inscriptionId = attendance.inscriptionId,
    year = attendance.year,
    month = attendance.month,
    column = attendance.column,
    value = attendance.value,
    playerId = attendance.playerId,
    id = attendance.id,
    player = player.asDomain()
)

@JvmName("ListAttendanceWithPlayerEntityAsDomain")
fun List<AttendanceWithPlayerEntity>.asDomain(): List<AttendanceWithPlayer> = map { it.asDomain() }

fun AttendanceSyncEntity.asDomain(): AttendanceSync = AttendanceSync(
    id = id
)

fun List<AttendanceSyncEntity>.asDomain(): List<AttendanceSync> = map { it.asDomain() }

fun AttendanceSync.asEntity(): AttendanceSyncEntity = AttendanceSyncEntity(
    id = id
)
