package com.golapp.attendances.data.datasources

import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.errors.AuthApiException
import com.golapp.attendances.data.remote.models.requests.AuthRequest
import com.golapp.attendances.data.remote.models.responses.LoginResponse
import com.google.gson.Gson
import javax.inject.Inject

class AuthRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI
) : AuthRemoteDataSource {
    override suspend fun auth(email: String, password: String): LoginResponse {
        val response = api.auth(AuthRequest(email, password))
        val body = response.body()

        if (response.isSuccessful && body != null) return body

        val errorBody = response.errorBody()
            ?.string()
            ?.takeIf { it.isNotBlank() }
            ?.let { raw ->
                runCatching {
                    Gson().fromJson(raw, LoginResponse::class.java).message
                }.getOrNull() ?: raw
            }

        throw AuthApiException(
            code = response.code(),
            serverMessage = errorBody ?: response.message()
        )
    }
}
