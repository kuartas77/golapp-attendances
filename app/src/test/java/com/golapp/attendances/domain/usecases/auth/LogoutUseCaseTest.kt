package com.golapp.attendances.domain.usecases.auth

import com.golapp.attendances.domain.models.User
import com.golapp.attendances.domain.repositories.AuthRepository
import com.golapp.attendances.domain.ui.LoginState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogoutUseCaseTest {

    @Test
    fun `logout does not complete before session cleanup finishes`() = runBlocking {
        val cleanupFinished = CompletableDeferred<Unit>()
        val repository = FakeAuthRepository(cleanupFinished)
        val useCase = LogoutUseCase(repository, Dispatchers.Unconfined)

        val logout = async { useCase() }
        yield()

        assertTrue(repository.logoutStarted)
        assertFalse(logout.isCompleted)

        cleanupFinished.complete(Unit)
        logout.await()

        assertTrue(logout.isCompleted)
    }

    private class FakeAuthRepository(
        private val cleanupFinished: CompletableDeferred<Unit>,
    ) : AuthRepository {
        var logoutStarted = false

        override suspend fun login(email: String, password: String) = LoginState.Success
        override suspend fun validateTokenExpiry() = true
        override suspend fun getUserData() = User("", 0, "", "", "")

        override suspend fun logout() {
            logoutStarted = true
            cleanupFinished.await()
        }

        override fun isLoggedIn(): Flow<Boolean> = flowOf(true)
    }
}
