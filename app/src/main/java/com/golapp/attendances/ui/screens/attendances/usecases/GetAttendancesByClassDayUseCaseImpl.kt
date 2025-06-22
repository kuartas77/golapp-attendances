package com.golapp.attendances.ui.screens.attendances.usecases

import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.repository.AttendanceRepository
import com.golapp.attendances.domain.usecases.attendances.GetAttendancesByClassDayUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAttendancesByClassDayUseCaseImpl @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : GetAttendancesByClassDayUseCase {
    override suspend fun invoke(classDay: ClassDay): Flow<List<AttendanceWithPlayer>> =
        attendanceRepository.getAttendances(classDay)
}
