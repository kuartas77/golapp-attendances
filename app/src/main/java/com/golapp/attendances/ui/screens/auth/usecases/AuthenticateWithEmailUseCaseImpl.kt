package com.golapp.attendances.ui.screens.auth.usecases

import com.golapp.attendances.domain.models.ResultLogin
import com.golapp.attendances.domain.repository.AuthRepository
import com.golapp.attendances.domain.usecases.authentication.AuthenticateWithEmailUseCase
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class AuthenticateWithEmailUseCaseImpl @Inject constructor(
    private val loginRepository: AuthRepository
) : AuthenticateWithEmailUseCase {
    override suspend fun invoke(
        email: String,
        password: String
    ): Flow<ResultLogin> {
        Timber.tag("AuthenticateWithEmailUseCaseImpl").d("email: $email, password: $password")
        return loginRepository.login(email, password)
    }
}
