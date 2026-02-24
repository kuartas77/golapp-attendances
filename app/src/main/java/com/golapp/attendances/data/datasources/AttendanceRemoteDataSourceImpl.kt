package com.golapp.attendances.data.datasources

import com.golapp.attendances.data.mappers.toDomain
import com.golapp.attendances.data.mappers.toRequest
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.models.ClassDay
import javax.inject.Inject

class AttendanceRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI
) : AttendanceRemoteDataSource {

    override suspend fun fetchAttendances(classDay: ClassDay): List<Attendance> {
        // Deja que Retrofit lance HttpException / IOException;
        // tu repository ya lo está atrapando (try/catch) 👍
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
}