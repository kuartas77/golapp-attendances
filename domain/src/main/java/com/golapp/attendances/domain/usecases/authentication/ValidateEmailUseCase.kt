package com.golapp.attendances.domain.usecases.authentication

interface ValidateEmailUseCase {
    operator fun invoke(email: String): Boolean
}
