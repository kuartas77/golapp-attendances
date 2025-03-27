package com.golapp.attendances.data.datasources.remote

import com.golapp.attendances.common.di.IoDispatcher
import com.golapp.attendances.common.resultOf
import com.golapp.attendances.data.local.models.AttendanceEntity
import com.golapp.attendances.data.mappers.asEntity
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.datasources.AttendancesRemoteDataSource
import com.golapp.attendances.data.remote.dto.RequestAttendance
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class AttendancesRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
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
                emit(emptyList<AttendanceEntity>())
            }
        }.onFailure {
            emit(emptyList<AttendanceEntity>())
        }

    }.onStart { emptyList<AttendanceEntity>() }.flowOn(ioDispatcher)

    override suspend fun sendAttendance(requestAttendance: RequestAttendance) {
        api.sendAttendance(requestAttendance)
    }

}
