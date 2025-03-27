package com.golapp.attendances.ui.screens.attendances.usecases

import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.domain.usecases.attendances.SyncAttendanceUseCase

class SyncAttendanceUseCaseImpl(
    private val attendanceRepository: AttendanceRepository
) : SyncAttendanceUseCase {
    override suspend fun invoke() {
        attendanceRepository.syncAttendances()
    }
}
