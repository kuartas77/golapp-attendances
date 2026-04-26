package com.golapp.attendances.data.remote.errors

import java.io.IOException

class AuthApiException(
    val code: Int,
    val serverMessage: String? = null
) : IOException(serverMessage ?: "Authentication request failed")
