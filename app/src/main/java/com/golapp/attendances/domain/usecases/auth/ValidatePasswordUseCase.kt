package com.golapp.attendances.domain.usecases.auth

class ValidatePasswordUseCase {
    operator fun invoke(password: String): Boolean = password.isNotBlank()
}