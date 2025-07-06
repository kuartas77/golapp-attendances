package com.golapp.attendances.data.remote.datasources

import com.golapp.attendances.data.local.models.AttendanceEntity
import com.golapp.attendances.data.remote.dto.RequestAttendance
import com.golapp.attendances.domain.models.ClassDay

interface AttendancesRemoteDataSource {
    suspend fun fetchAttendances(classDay: ClassDay): List<AttendanceEntity>

    suspend fun sendAttendance(requestAttendance: RequestAttendance)
}
