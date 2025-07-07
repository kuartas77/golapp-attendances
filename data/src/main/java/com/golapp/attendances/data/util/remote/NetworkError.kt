package com.golapp.attendances.data.util.remote

sealed interface NetworkError {
    data object Connectivity : NetworkError
    data object Unknown : NetworkError
    data class Server(val code: Int, val message: String) : NetworkError
}
