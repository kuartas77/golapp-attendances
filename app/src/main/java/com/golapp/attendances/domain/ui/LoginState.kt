package com.golapp.attendances.domain.ui

sealed interface LoginState {
    data object Success : LoginState
    data class Error(
        val reason: Reason,
        val code: Int? = null
    ) : LoginState

    enum class Reason {
        INVALID_CREDENTIALS,
        CONNECTION,
        SERVER,
        UNKNOWN
    }
}
