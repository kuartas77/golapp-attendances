package com.golapp.attendances.feature.auth

import androidx.lifecycle.SavedStateHandle
import com.golapp.attendances.domain.models.User
import com.golapp.attendances.domain.repositories.AuthRepository
import com.golapp.attendances.domain.ui.LoginState
import com.golapp.attendances.domain.usecases.auth.AuthUseCases
import com.golapp.attendances.domain.usecases.auth.AuthenticateWithEmailUseCase
import com.golapp.attendances.domain.usecases.auth.CheckLoginUseCase
import com.golapp.attendances.domain.usecases.auth.GetUserDataUseCase
import com.golapp.attendances.domain.usecases.auth.LogoutUseCase
import com.golapp.attendances.domain.usecases.auth.ValidateEmailUseCase
import com.golapp.attendances.domain.usecases.auth.ValidatePasswordUseCase
import com.golapp.attendances.domain.usecases.auth.ValidateTokenExpiryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AuthViewModelRestorationTest {

    @Test
    fun `restored email wins over injected fallback without restoring password`() {
        val handle = SavedStateHandle(mapOf("auth.email" to "restored@example.com"))

        val viewModel = AuthViewModel(
            authUseCases = authUseCases(),
            ioDispatcher = Dispatchers.Unconfined,
            savedStateHandle = handle,
            initialState = AuthUiState(email = "fallback@example.com", password = "never-restore"),
        )

        assertEquals("restored@example.com", viewModel.uiState.value.email)
        assertEquals("never-restore", viewModel.uiState.value.password)
        assertFalse(handle.contains("auth.password"))
    }

    @Test
    fun `accepted email transition updates memory and saved state together`() {
        val handle = SavedStateHandle()
        val viewModel = AuthViewModel(
            authUseCases = authUseCases(),
            ioDispatcher = Dispatchers.Unconfined,
            savedStateHandle = handle,
            initialState = AuthUiState(),
        )

        viewModel.onEvent(AuthUiEvent.EmailChanged("new@example.com"))

        assertEquals("new@example.com", viewModel.uiState.value.email)
        assertEquals("new@example.com", handle.get<String>("auth.email"))
    }

    private fun authUseCases(): AuthUseCases {
        val repository = FakeAuthRepository()
        return AuthUseCases(
            validateEmail = ValidateEmailUseCase(),
            validatePassword = ValidatePasswordUseCase(),
            loginWithEmail = AuthenticateWithEmailUseCase(repository),
            validateTokenExpiryUseCase = ValidateTokenExpiryUseCase(repository),
            getUserDataUseCase = GetUserDataUseCase(repository),
            checkLoginUseCase = CheckLoginUseCase(repository),
            logoutUseCase = LogoutUseCase(repository, Dispatchers.Unconfined),
        )
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String) = LoginState.Success
        override suspend fun validateTokenExpiry() = false
        override suspend fun getUserData() = User("", 0, "", "", "")
        override suspend fun logout() = Unit
        override fun isLoggedIn(): Flow<Boolean> = flowOf(false)
    }
}
