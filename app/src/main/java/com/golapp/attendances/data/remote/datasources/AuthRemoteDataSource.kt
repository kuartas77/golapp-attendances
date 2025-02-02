package com.golapp.attendances.data.remote.datasources

import com.golapp.attendances.data.remote.dto.ResponseLogin

interface AuthRemoteDataSource {
    suspend fun authenticate(email: String, password: String): ResponseLogin
}
