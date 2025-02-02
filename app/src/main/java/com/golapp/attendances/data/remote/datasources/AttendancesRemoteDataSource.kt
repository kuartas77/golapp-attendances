package com.golapp.attendances.data.remote.datasources

import com.golapp.attendances.data.local.models.AttendanceEntity
import kotlinx.coroutines.flow.Flow

interface AttendancesRemoteDataSource {
    suspend fun fetchAttendances(
        groupId: Int,
        month: Int,
        column: String,
        schoolId: Int
    ): Flow<List<AttendanceEntity>>
}
