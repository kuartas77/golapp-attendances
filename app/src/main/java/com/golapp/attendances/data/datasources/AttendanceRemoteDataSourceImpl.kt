package com.golapp.attendances.data.datasources

import com.golapp.attendances.data.mappers.toDomain
import com.golapp.attendances.data.mappers.toRequest
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.models.dtos.toDomain
import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.models.Statistics
import javax.inject.Inject

class AttendanceRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI
) : AttendanceRemoteDataSource {

    override suspend fun fetchAttendances(classDay: ClassDay): List<Attendance> {
        val res = api.getAttendances(
            schoolId = classDay.schoolId,
            groupId = classDay.groupId,
            month = classDay.month,
            column = classDay.column
        )

        return res.data.orEmpty().map { it.toDomain() }
    }

    override suspend fun syncAttendance(attendance: Attendance) {
        api.syncAttendance(attendance.toRequest())
    }

    override suspend fun fetchStatistics(): List<Statistics> {
        val res = api.getAttendanceStatistics()
        return res.data.orEmpty().map { it.toDomain() }
    }
}