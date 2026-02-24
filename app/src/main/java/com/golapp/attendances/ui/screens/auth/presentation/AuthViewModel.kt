package com.golapp.attendances.ui.screens.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.BuildConfig
import com.golapp.attendances.R
import com.golapp.attendances.common.events.UiText
import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.ui.LoginState
import com.golapp.attendances.domain.usecases.auth.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        checkTokenExpiry()
    }

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.EmailChanged -> _uiState.update {
                it.copy(
                    email = event.email,
                    errorEmail = null,
                    error = null
                )
            }

            is AuthUiEvent.PasswordChanged -> _uiState.update {
                it.copy(
                    password = event.password,
                    errorPassword = null,
                    error = null
                )
            }

            AuthUiEvent.LoginClicked -> validateAndLogin()
        }
    }

    private fun checkTokenExpiry() {
        viewModelScope.launch(ioDispatcher) {
            setLoading(true)

            val valid = authUseCases.validateTokenExpiryUseCase()

            if (valid) {
                _uiState.update { it.copy(isLoggedIn = true, isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoggedIn = false,
                        isLoading = false,
                        errorEmail = UiText.StringResource(resId = R.string.session_expired)
                    )
                }
            }
        }
    }

    private fun validateAndLogin() {
        // limpiar errores previos
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
            setLoading(true)

            val result = authUseCases.loginWithEmail(email, password)

            applyLoginResult(result)

            setLoading(false)
        }
    }

    private fun applyLoginResult(result: LoginState) {
        when (result) {
            LoginState.Success -> {
                _uiState.update { it.copy(isLoggedIn = true) }
            }

            is LoginState.Error -> {
                when (result.code) {
                    422 -> {
                        _uiState.update {
                            it.copy(
                                isLoggedIn = false,
                                errorEmail = UiText.StringResource(R.string.error_credential_is_not_valid)
                            )
                        }
                    }

                    else -> {
                        _uiState.update {
                            it.copy(
                                isLoggedIn = false,
                                error = result.message ?: "Error de autenticación"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun setLoading(value: Boolean) {
        _uiState.update { it.copy(isLoading = value) }
    }
}

sealed interface AuthUiEvent {
    data class EmailChanged(val email: String) : AuthUiEvent
    data class PasswordChanged(val password: String) : AuthUiEvent
    data object LoginClicked : AuthUiEvent
}

data class AuthUiState(
    val email: String = if (BuildConfig.DEBUG) "kuartas77@gmail.com" else "",
    val password: String = if (BuildConfig.DEBUG) "Coco-.lizo1017" else "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val errorPassword: UiText? = null,
    val errorEmail: UiText? = null
)
