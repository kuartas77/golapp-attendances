package com.golapp.attendances.common.ui

sealed class UiState<out T : Any> {
    data object Loading : UiState<Nothing>()
    data class Success<out T : Any>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    data object Idle : UiState<Nothing>()
}
