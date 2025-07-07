package com.golapp.attendances.domain.repository

import com.golapp.attendances.data.local.models.AttendanceEntity
import com.golapp.attendances.data.remote.dto.RequestAttendance
import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun insertAttendance(attendance: Attendance)
    suspend fun insertAttendanceSync(attendanceSync: AttendanceSync)
    suspend fun insertAttendances(attendances: List<Attendance>)
    suspend fun getAttendanceById(id: Long): Attendance
    suspend fun getAttendancesSync(): List<AttendanceSync>
    suspend fun getAttendancesWithPlayers(classDay: ClassDay): List<AttendanceWithPlayer>
    suspend fun getAttendances(classDay: ClassDay): Flow<List<AttendanceWithPlayer>>
    suspend fun getAllAttendances(): List<AttendanceEntity>
    suspend fun deleteAttendance(attendance: Attendance)
    suspend fun deleteAllAttendances()
    suspend fun deleteAttendanceSync(attendanceSync: AttendanceSync)
    suspend fun sendAttendance(requestAttendance: RequestAttendance)
    suspend fun fetchAttendances(classDay: ClassDay): List<Attendance>
}
