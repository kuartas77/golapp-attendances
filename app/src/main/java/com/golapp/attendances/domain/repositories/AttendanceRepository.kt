package com.golapp.attendances.domain.repositories

import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun getAttendancesWithPlayers(classDay: ClassDay, year: Int): List<AttendanceWithPlayer>
    fun observeAttendancesWithPlayers(classDay: ClassDay, year: Int): Flow<List<AttendanceWithPlayer>>
    suspend fun fetchAttendances(classDay: ClassDay, year: Int): List<Attendance>
    suspend fun upsertAttendances(items: List<Attendance>)
    suspend fun insertAttendances(items: List<Attendance>)
    suspend fun updateAttendanceValue(localAttendanceId: Long, value: String?)
    suspend fun getAllAttendances(): List<Attendance>
    suspend fun getAllAttendanceSync(): List<AttendanceSync>
    suspend fun insertAttendancesSync(items: List<AttendanceSync>)
    suspend fun deleteAttendanceSync(item: AttendanceSync)
    suspend fun getAttendanceById(id: Long): Attendance
    suspend fun syncAttendance(attendance: Attendance)
}
