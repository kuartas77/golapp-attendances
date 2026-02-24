package com.golapp.attendances.domain.usecases.auth

import android.util.Patterns

class ValidateEmailUseCase {
    operator fun invoke(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
}