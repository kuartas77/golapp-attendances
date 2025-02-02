package com.golapp.attendances.data.remote.models

data class DataResponse<T>(
    val data: T,
    val message: String? = null,
    val code: Int? = null
)
