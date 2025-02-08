package com.golapp.attendances.ui.screens.attendances.usecases

import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.domain.usecases.attendances.VerifyAttendancesByClassIdUseCase
import javax.inject.Inject

class VerifyAttendancesByClassIdUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : VerifyAttendancesByClassIdUseCase {
    override suspend fun invoke(classDay: ClassDay) {
        attendanceRepository.verifyAttendancesByClassDayId(classDay)
    }
}
