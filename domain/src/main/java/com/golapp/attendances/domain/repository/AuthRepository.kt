package com.golapp.attendances.domain.repository

import com.golapp.attendances.domain.models.ResultLogin
import com.golapp.attendances.domain.models.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): Flow<ResultLogin>
    suspend fun validateTokenExpiry(): Boolean
    suspend fun getUserData(): Flow<User>
    suspend fun logout()
}
