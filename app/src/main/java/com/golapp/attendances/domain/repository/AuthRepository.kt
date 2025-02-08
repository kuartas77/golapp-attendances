package com.golapp.attendances.domain.repository

import com.golapp.attendances.domain.models.ResultLogin
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<ResultLogin>
    suspend fun validateTokenExpiry(): Boolean
    suspend fun logout()
}
