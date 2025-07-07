package com.golapp.attendances.domain.usecases.authentication

interface ValidatePasswordUseCase {
    operator fun invoke(password: String): Boolean
}
