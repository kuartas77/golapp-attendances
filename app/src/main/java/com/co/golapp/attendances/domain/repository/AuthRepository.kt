package com.co.golapp.attendances.domain.repository

import com.co.golapp.attendances.domain.models.ResultLogin
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<ResultLogin>
    suspend fun validateTokenExpiry(): Boolean
}
