package com.golapp.attendances.data.datasources.remote

import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.datasources.AuthRemoteDataSource
import com.golapp.attendances.data.remote.dto.RequestAuth
import com.golapp.attendances.data.remote.dto.ResponseLogin
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI
) : AuthRemoteDataSource {
    override suspend fun authenticate(
        email: String,
        password: String
    ): ResponseLogin {

        val response = api.login(RequestAuth(email, password))
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            ResponseLogin(
                token = "",
                refreshToken = "",
                type = "",
                expires = 0L,
                message = response.message(),
                code = response.code(),
                userDto = null,
            )
        }
    }
}
