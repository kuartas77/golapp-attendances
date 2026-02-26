package com.golapp.attendances.domain.usecases.auth

import com.golapp.attendances.domain.repositories.AuthRepository
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.getUserData()
}