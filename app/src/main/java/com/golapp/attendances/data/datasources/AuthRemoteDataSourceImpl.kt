package com.golapp.attendances.data.datasources

import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.models.requests.AuthRequest
import com.golapp.attendances.data.remote.models.responses.LoginResponse
import javax.inject.Inject

class AuthRemoteDataSourceImpl  @Inject constructor(
    private val api: GolappAPI
) : AuthRemoteDataSource {
    override suspend fun auth(email: String, password: String): LoginResponse {
        val response = api.auth(AuthRequest(email, password))
        val body = response.body()

        if (response.isSuccessful && body != null) return body

        // aquí puedes crear una excepción con mensaje más útil
        throw RuntimeException(
            response.message().ifBlank { "Login falló (HTTP ${response.code()})" }
        )
    }
}