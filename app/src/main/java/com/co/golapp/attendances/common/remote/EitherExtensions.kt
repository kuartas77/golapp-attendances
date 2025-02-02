package com.co.golapp.attendances.common.remote

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

fun Throwable.toNetworkError(): NetworkError {
    return when (this) {
        is IOException -> NetworkError.Connectivity
        is HttpException -> NetworkError.Server(this.code(), this.message())
        else -> NetworkError.Unknown
    }
}

suspend fun <T> tryCall(action: suspend () -> T): Either<NetworkError, T> = try {
    action().right()
} catch (e: Exception) {
    Timber.tag("error").e(e.toString())
    e.toNetworkError().left()
}
