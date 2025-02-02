package com.co.golapp.attendances.data.datasources.remote

import com.co.golapp.attendances.common.remote.NetworkResult
import com.co.golapp.attendances.data.remote.GolappAPI
import com.co.golapp.attendances.data.remote.datasources.AuthRemoteDataSource
import com.co.golapp.attendances.data.remote.dto.RequestAuth
import com.co.golapp.attendances.data.remote.dto.ResponseLogin
import com.co.golapp.attendances.common.remote.handleApi
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI
) : AuthRemoteDataSource {
    override suspend fun authenticate(
        email: String,
        password: String
    ): NetworkResult<ResponseLogin> = handleApi { api.login(RequestAuth(email, password)) }
}
