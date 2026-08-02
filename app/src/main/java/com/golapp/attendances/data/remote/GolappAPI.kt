package com.golapp.attendances.data.remote

import com.golapp.attendances.core.common.Constants.ATTENDANCES
import com.golapp.attendances.core.common.Constants.AUTH
import com.golapp.attendances.core.common.Constants.GROUPS
import com.golapp.attendances.core.common.Constants.STATISTICS
import com.golapp.attendances.core.common.Constants.UPDATE_ATTENDANCE
import com.golapp.attendances.data.remote.models.dtos.AttendanceDto
import com.golapp.attendances.data.remote.models.dtos.GroupDto
import com.golapp.attendances.data.remote.models.dtos.StatisticsDto
import com.golapp.attendances.data.remote.models.requests.AttendanceRequest
import com.golapp.attendances.data.remote.models.requests.AuthRequest
import com.golapp.attendances.data.remote.models.responses.ApiData
import com.golapp.attendances.data.remote.models.responses.LoginResponse
import com.golapp.attendances.core.di.Authorized
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface GolappAPI {

    @POST(AUTH)
    suspend fun auth(@Body request: AuthRequest): Response<LoginResponse>

    @Authorized
    @GET(GROUPS)
    suspend fun getAllGroupsSnapshot(): ApiData<List<GroupDto>>

    @Authorized
    @GET(ATTENDANCES)
    suspend fun getAttendances(
        @Query("training_group_id") groupId: Int,
        @Query("month") month: Int,
        @Query("column") column: String,
        @Query("school_id") schoolId: Int
    ): ApiData<List<AttendanceDto>>

    @Authorized
    @POST(UPDATE_ATTENDANCE)
    suspend fun syncAttendance(@Body request: AttendanceRequest): ApiData<Boolean>

    @Authorized
    @GET(STATISTICS)
    suspend fun getAttendanceStatistics(): ApiData<List<StatisticsDto>>
}
