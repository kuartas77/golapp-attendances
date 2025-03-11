package com.golapp.attendances.ui.screens.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.golapp.attendances.BuildConfig
import com.golapp.attendances.R
import com.golapp.attendances.common.di.IoDispatcher
import com.golapp.attendances.common.ui.events.UiText
import com.golapp.attendances.ui.screens.auth.usecases.AuthenticationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authenticationUseCases: AuthenticationUseCases,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher

) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        checkTokenExpiry()
    }

    private fun checkTokenExpiry() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true) }
            if (authenticationUseCases.validateTokenExpiry()) {
                _uiState.update { it.copy(isLoggedIn = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = false,
                        errorEmail = UiText.StringResource(resId = R.string.session_expired)
                    )
                }
            }
        }
    }

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.EmailChanged -> _uiState.update { it.copy(email = event.email) }
            AuthUiEvent.LoginClicked -> auth()
            is AuthUiEvent.PasswordChanged -> _uiState.update { it.copy(password = event.password) }
        }
    }

    private fun auth() {
        _uiState.update { it.copy(errorEmail = null, errorPassword = null) }

        if (authenticationUseCases.validateEmail(uiState.value.email).not()) {
            _uiState.update { it.copy(errorEmail = UiText.StringResource(resId = R.string.invalid_email)) }
        }
        if (authenticationUseCases.validatePassword(uiState.value.password).not()) {
            _uiState.update { it.copy(errorPassword = UiText.StringResource(resId = R.string.invalid_password)) }
        }

        if (_uiState.value.errorEmail === null && _uiState.value.errorPassword === null) {
            processLogin()
        }
    }

    private fun processLogin() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch(ioDispatcher) {
            authenticationUseCases.loginWithEmail(
                email = uiState.value.email,
                password = uiState.value.password
            ).collect { resultLogin ->
                when (resultLogin.code) {
                    200 -> {
                        _uiState.update { it.copy(isLoggedIn = resultLogin.idle) }
                    }

                    422 -> {
                        _uiState.update {
                            it.copy(errorEmail = UiText.StringResource(R.string.error_credential_is_not_valid))
                        }
                    }

                    else -> {
                        _uiState.update { it.copy(error = resultLogin.message) }
                    }
                }
                _uiState.update { it.copy(isLoggedIn = resultLogin.idle, isLoading = false) }
            }
        }
    }
}

sealed interface AuthUiEvent {
    data class EmailChanged(val email: String) : AuthUiEvent
    data class PasswordChanged(val password: String) : AuthUiEvent
    data object LoginClicked : AuthUiEvent
}

data class AuthUiState(
    val email: String = if (BuildConfig.DEBUG) "juan_londono84151@elpoli.edu.co" else "",
    val password: String = if (BuildConfig.DEBUG) "Coco-.lizo1017" else "",
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val errorPassword: UiText? = null,
    val errorEmail: UiText? = null
)
