package com.golapp.attendances.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.R
import com.golapp.attendances.core.common.events.UiText
import com.golapp.attendances.core.coroutines.rethrowIfCancellation
import com.golapp.attendances.core.di.IoDispatcher
import com.golapp.attendances.domain.ui.LoginState
import com.golapp.attendances.domain.usecases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle,
    initialState: AuthUiState,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        initialState.copy(email = savedStateHandle[KEY_EMAIL] ?: initialState.email)
    )
    val uiState = _uiState.asStateFlow()
    private val loginMutex = Mutex()
    private var started = false

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            AuthUiEvent.Start -> start()
            is AuthUiEvent.EmailChanged -> updateState {
                copy(
                    email = event.email,
                    errorEmail = null,
                    error = null
                )
            }

            is AuthUiEvent.PasswordChanged -> updateState {
                copy(
                    password = event.password,
                    errorPassword = null,
                    error = null
                )
            }

            AuthUiEvent.LoginClicked -> validateAndLogin()
        }
    }

    private fun start() {
        if (started) return
        started = true
        checkTokenExpiry()
    }

    private fun checkTokenExpiry() {
        viewModelScope.launch(ioDispatcher) {
            try {
                setLoading(true)
                val hasSession = authUseCases.checkLoginUseCase().first()
                if (!hasSession) {
                    updateState {
                        copy(
                            isLoggedIn = false,
                            isLoading = false,
                            error = null
                        )
                    }
                    return@launch
                }
                val valid = authUseCases.validateTokenExpiryUseCase()
                updateState {
                    if (valid) copy(isLoggedIn = true, isLoading = false, error = null)
                    else copy(
                        isLoggedIn = false,
                        isLoading = false,
                        error = UiText.StringResource(resId = R.string.session_expired)
                    )
                }
            } catch (error: Exception) {
                error.rethrowIfCancellation()
                updateState {
                    copy(isLoggedIn = false, isLoading = false, error = UiText.StringResource(R.string.unknown_error))
                }
            }
        }
    }

    private fun validateAndLogin() {
        _uiState.update { it.copy(errorEmail = null, errorPassword = null, error = null) }

        val email = uiState.value.email
        val password = uiState.value.password

        var hasError = false

        if (!authUseCases.validateEmail(email)) {
            hasError = true
            _uiState.update { it.copy(errorEmail = UiText.StringResource(resId = R.string.invalid_email)) }
        }
        if (!authUseCases.validatePassword(password)) {
            hasError = true
            _uiState.update { it.copy(errorPassword = UiText.StringResource(resId = R.string.invalid_password)) }
        }

        if (!hasError) {
            processLogin(email, password)
        }
    }

    private fun processLogin(email: String, password: String) {
        viewModelScope.launch(ioDispatcher) {
            if (!loginMutex.tryLock()) return@launch
            try {
                setLoading(true)
                applyLoginResult(authUseCases.loginWithEmail(email, password))
            } catch (error: Exception) {
                error.rethrowIfCancellation()
                updateState {
                    copy(isLoggedIn = false, error = UiText.StringResource(R.string.unknown_error))
                }
            } finally {
                setLoading(false)
                loginMutex.unlock()
            }
        }
    }

    private fun applyLoginResult(result: LoginState) {
        when (result) {
            LoginState.Success -> {
                _uiState.update { it.copy(isLoggedIn = true) }
            }

            is LoginState.Error -> {
                _uiState.update {
                    it.copy(
                        isLoggedIn = false,
                        errorEmail = when (result.reason) {
                            LoginState.Reason.INVALID_CREDENTIALS ->
                                UiText.StringResource(R.string.error_credential_is_not_valid)

                            else -> null
                        },
                        error = when (result.reason) {
                            LoginState.Reason.INVALID_CREDENTIALS -> null
                            LoginState.Reason.CONNECTION ->
                                UiText.StringResource(R.string.error_connection)

                            LoginState.Reason.SERVER ->
                                UiText.StringResource(R.string.server_error)

                            LoginState.Reason.UNKNOWN ->
                                UiText.StringResource(R.string.unknown_error)
                        }
                    )
                }
            }
        }
    }

    private fun setLoading(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }

    private inline fun updateState(transform: AuthUiState.() -> AuthUiState) {
        _uiState.update { it.transform() }
        savedStateHandle[KEY_EMAIL] = _uiState.value.email
    }

    private companion object {
        const val KEY_EMAIL = "auth.email"
    }
}

sealed interface AuthUiEvent {
    data object Start : AuthUiEvent
    data class EmailChanged(val email: String) : AuthUiEvent
    data class PasswordChanged(val password: String) : AuthUiEvent
    data object LoginClicked : AuthUiEvent
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: UiText? = null,
    val errorPassword: UiText? = null,
    val errorEmail: UiText? = null
)
