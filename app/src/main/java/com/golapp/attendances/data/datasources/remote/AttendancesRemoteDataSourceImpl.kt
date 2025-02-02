package com.golapp.attendances.data.datasources.remote

import com.golapp.attendances.common.resultOf
import com.golapp.attendances.data.local.models.AttendanceEntity
import com.golapp.attendances.data.mappers.asEntity
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.datasources.AttendancesRemoteDataSource
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AttendancesRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI
) : AttendancesRemoteDataSource {
    override suspend fun fetchAttendances(
        groupId: Int,
        month: Int,
        column: String,
        schoolId: Int
    ) = flow<List<AttendanceEntity>> {
        resultOf {
            val response = api.fetchAttendances(groupId, month, column, schoolId)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(body.asEntity())
            } else {
                emit(emptyList())
            }
        }.onFailure {
            emit(emptyList())
        }

    }

}
