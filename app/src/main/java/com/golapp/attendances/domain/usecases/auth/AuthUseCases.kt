package com.golapp.attendances.domain.usecases.auth

data class AuthUseCases(
    val validateEmail: ValidateEmailUseCase,
    val validatePassword: ValidatePasswordUseCase,
    val loginWithEmail: AuthenticateWithEmailUseCase,
    val validateTokenExpiryUseCase: ValidateTokenExpiryUseCase,
    val getUserDataUseCase: GetUserDataUseCase,
    val logoutUseCase: LogoutUseCase
)
