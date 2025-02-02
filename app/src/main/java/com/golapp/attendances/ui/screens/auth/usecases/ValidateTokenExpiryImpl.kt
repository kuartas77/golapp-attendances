package com.golapp.attendances.ui.screens.auth.usecases

import com.golapp.attendances.domain.repository.AuthRepository
import com.golapp.attendances.domain.usecases.authentication.ValidateTokenExpiry
import javax.inject.Inject

class ValidateTokenExpiryImpl @Inject constructor(
    private val loginRepository: AuthRepository
) : ValidateTokenExpiry {
    override suspend fun invoke(): Boolean = loginRepository.validateTokenExpiry()
}
