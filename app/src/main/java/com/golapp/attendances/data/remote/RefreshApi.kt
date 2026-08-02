package com.golapp.attendances.data.remote

import com.golapp.attendances.core.common.Constants.REFRESH
import com.golapp.attendances.data.remote.models.responses.LoginResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface RefreshApi {

    @POST(REFRESH)
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Response<LoginResponse>
}