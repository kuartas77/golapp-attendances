package com.golapp.attendances.data.datasources

import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.ClassDay

interface AttendanceRemoteDataSource   {
    suspend fun fetchAttendances(classDay: ClassDay): List<Attendance>
    suspend fun syncAttendance(attendance: Attendance)
}