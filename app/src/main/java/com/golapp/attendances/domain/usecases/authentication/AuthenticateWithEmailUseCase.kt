package com.golapp.attendances.domain.usecases.authentication

import com.golapp.attendances.domain.models.ResultLogin
import kotlinx.coroutines.flow.Flow

interface AuthenticateWithEmailUseCase {
    suspend operator fun invoke(email: String, password: String): Flow<ResultLogin>
}
