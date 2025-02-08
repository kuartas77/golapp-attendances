package com.golapp.attendances.ui.screens.attendances.usecases

import com.golapp.attendances.domain.models.Attendance
import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.domain.usecases.attendances.TakeAttendanceUseCase
import javax.inject.Inject

class TakeAttendanceUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : TakeAttendanceUseCase {
    override suspend fun invoke(attendance: Attendance) =
        attendanceRepository.insertAttendance(attendance)
}
