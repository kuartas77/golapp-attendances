package com.golapp.attendances.data.datasources.remote

import com.golapp.attendances.data.local.models.AttendanceEntity
import com.golapp.attendances.data.mappers.asEntity
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.datasources.AttendancesRemoteDataSource
import com.golapp.attendances.data.remote.dto.RequestAttendance
import com.golapp.attendances.domain.models.ClassDay
import javax.inject.Inject

class AttendancesRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI
) : AttendancesRemoteDataSource {
    override suspend fun fetchAttendances(classDay: ClassDay): List<AttendanceEntity> {
        val response = api.fetchAttendances(
            classDay.groupId,
            classDay.month,
            classDay.column,
            classDay.schoolId
        )
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body.asEntity()
        } else {
            emptyList()
        }
    }

    override suspend fun sendAttendance(requestAttendance: RequestAttendance) {
        api.sendAttendance(requestAttendance)
    }

}
