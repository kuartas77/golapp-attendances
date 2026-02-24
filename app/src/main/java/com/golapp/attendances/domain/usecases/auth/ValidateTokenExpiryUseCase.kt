package com.golapp.attendances.domain.usecases.auth

import com.golapp.attendances.domain.repositories.AuthRepository
import javax.inject.Inject

class ValidateTokenExpiryUseCase @Inject constructor(
    private val authRepository: AuthRepository
){
    suspend operator fun invoke(): Boolean = authRepository.validateTokenExpiry()
}