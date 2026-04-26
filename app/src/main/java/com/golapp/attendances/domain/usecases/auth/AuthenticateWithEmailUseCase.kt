package com.golapp.attendances.domain.usecases.auth

import com.golapp.attendances.domain.repositories.AuthRepository
import com.golapp.attendances.domain.ui.LoginState
import timber.log.Timber
import javax.inject.Inject

class AuthenticateWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): LoginState {
        Timber.tag("AuthenticateWithEmailUseCaseImpl").d("Trying login for email: $email")
        return authRepository.login(email, password)
    }
}
