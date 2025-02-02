package com.golapp.attendances.ui.screens.auth.usecases

import com.golapp.attendances.domain.usecases.authentication.ValidatePasswordUseCase

class ValidatePasswordUseCaseImpl : ValidatePasswordUseCase {
    override fun invoke(password: String): Boolean = password.isNotBlank()
}
