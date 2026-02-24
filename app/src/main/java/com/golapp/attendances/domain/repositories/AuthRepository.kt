package com.golapp.attendances.domain.repositories

import com.golapp.attendances.domain.models.User
import com.golapp.attendances.domain.ui.LoginState

interface AuthRepository {
    suspend fun login(email: String, password: String): LoginState
    suspend fun validateTokenExpiry(): Boolean
    suspend fun getUserData(): User
    suspend fun logout()
}