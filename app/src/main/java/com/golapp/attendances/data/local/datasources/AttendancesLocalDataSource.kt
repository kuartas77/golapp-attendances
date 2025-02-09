package com.golapp.attendances.data.local.datasources

import com.golapp.attendances.data.local.models.AttendanceEntity
import com.golapp.attendances.data.local.models.AttendanceSyncEntity
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import kotlinx.coroutines.flow.Flow

interface AttendancesLocalDataSource {
    suspend fun insertAttendance(attendance: AttendanceEntity)
    suspend fun insertAttendances(attendances: List<AttendanceEntity>)
    suspend fun insertAttendanceSync(attendanceSync: AttendanceSyncEntity)
    suspend fun getAttendancesWithPlayers(
        groupId: Int,
        month: Int,
        column: String,
        schoolId: Int
    ): List<AttendanceWithPlayer>

    suspend fun getAttendancesSync(): List<AttendanceSync>
    suspend fun deleteAttendance(attendance: AttendanceEntity)
    suspend fun deleteAttendanceSync(attendanceSyncEntity: AttendanceSyncEntity)
    suspend fun deleteAttendances()
    fun getAttendances(
        groupId: Int,
        month: Int,
        column: String,
        schoolId: Int
    ): Flow<List<AttendanceWithPlayer>>

    suspend fun getAttendanceById(id: Long): AttendanceEntity
}
