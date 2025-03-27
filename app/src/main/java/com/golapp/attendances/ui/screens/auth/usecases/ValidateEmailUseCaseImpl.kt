package com.golapp.attendances.ui.screens.auth.usecases

import android.util.Patterns
import com.golapp.attendances.domain.usecases.authentication.ValidateEmailUseCase

class ValidateEmailUseCaseImpl : ValidateEmailUseCase {
    override fun invoke(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
