package com.golapp.attendances.ui.screens.auth.usecases

import com.golapp.attendances.domain.usecases.authentication.AuthenticateWithEmailUseCase
import com.golapp.attendances.domain.usecases.authentication.ValidateEmailUseCase
import com.golapp.attendances.domain.usecases.authentication.ValidatePasswordUseCase
import com.golapp.attendances.domain.usecases.authentication.ValidateTokenExpiry
import javax.inject.Inject

data class AuthenticationUseCases @Inject constructor(
    val validateEmail: ValidateEmailUseCase,
    val validatePassword: ValidatePasswordUseCase,
    val loginWithEmail: AuthenticateWithEmailUseCase,
    val validateTokenExpiry: ValidateTokenExpiry
)
