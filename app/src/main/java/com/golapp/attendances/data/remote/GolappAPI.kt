package com.golapp.attendances.data.remote

import com.golapp.attendances.common.Constants.ATTENDANCES
import com.golapp.attendances.common.Constants.AUTH
import com.golapp.attendances.common.Constants.GROUPS
import com.golapp.attendances.common.di.Authorized
import com.golapp.attendances.data.remote.dto.RequestAuth
import com.golapp.attendances.data.remote.dto.ResponseAttendances
import com.golapp.attendances.data.remote.dto.ResponseGroup
import com.golapp.attendances.data.remote.dto.ResponseGroups
import com.golapp.attendances.data.remote.dto.ResponseLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GolappAPI {

    @POST(AUTH)
    suspend fun login(@Body request: RequestAuth): Response<ResponseLogin>

    @Authorized
    @GET(GROUPS)
    suspend fun fetchGroups(): Response<ResponseGroups>

    @Authorized
    @GET(GROUPS)
    suspend fun fetchGroup(@Path("groupId") groupId: Int): Response<ResponseGroup>

    @Authorized
    @GET(ATTENDANCES)
    suspend fun fetchAttendances(
        @Query("training_group_id") groupId: Int,
        @Query("month") month: Int,
        @Query("column") column: String,
        @Query("school_id") schoolId: Int
    ): Response<ResponseAttendances>
}
