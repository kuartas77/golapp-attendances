package com.golapp.attendances.domain.usecases.authentication

interface ValidateTokenExpiry {
    suspend operator fun invoke(): Boolean
}
