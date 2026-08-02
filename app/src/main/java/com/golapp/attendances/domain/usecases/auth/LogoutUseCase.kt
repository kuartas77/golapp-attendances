package com.golapp.attendances.domain.usecases.auth

import com.golapp.attendances.core.di.IoDispatcher
import com.golapp.attendances.domain.repositories.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() = withContext(ioDispatcher + NonCancellable) {
        authRepository.logout()
        delay(100)
    }
}