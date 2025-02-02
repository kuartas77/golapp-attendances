package com.golapp.attendances.domain.repository

import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.AttendanceSync
import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun verifyAttendancesByClassDayId(classDay: ClassDay)
    suspend fun getClassDayById(id: String): ClassDay
    fun getAttendances(classDay: ClassDay): Flow<List<AttendanceWithPlayer>>
    suspend fun insertAttendance(attendance: Attendance)
    suspend fun insertAttendanceSync(attendanceSync: AttendanceSync)
    suspend fun insertAttendances(attendances: List<Attendance>)
    suspend fun deleteAttendance(attendance: Attendance)
    suspend fun deleteAttendances()
}
