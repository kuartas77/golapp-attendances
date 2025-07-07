package com.golapp.attendances.domain.usecases.attendances

interface SyncAttendanceUseCase {
    suspend operator fun invoke()
}
