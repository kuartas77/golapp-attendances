package com.golapp.attendances.data.datasources

import com.golapp.attendances.data.remote.models.responses.LoginResponse

interface AuthRemoteDataSource {
    suspend fun auth(email: String, password: String): LoginResponse
}