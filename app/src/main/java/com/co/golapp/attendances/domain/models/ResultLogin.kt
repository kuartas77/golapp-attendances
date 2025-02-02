package com.co.golapp.attendances.domain.models

data class ResultLogin(
    val idle: Boolean = false,
    val message: String? = null,
    val code: Int? = null,
)
