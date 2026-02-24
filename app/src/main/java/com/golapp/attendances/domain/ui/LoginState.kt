package com.golapp.attendances.domain.ui

sealed interface LoginState {
    data object Success : LoginState
    data class Error(val code: Int? = null, val message: String? = null) : LoginState
}