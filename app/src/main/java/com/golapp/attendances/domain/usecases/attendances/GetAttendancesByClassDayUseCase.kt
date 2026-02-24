package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.repositories.AttendanceRepository
import com.golapp.attendances.domain.time.YearProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetAttendancesByClassDayUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val yearProvider: YearProvider,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(classDay: ClassDay) = withContext(ioDispatcher) {
        attendanceRepository.observeAttendancesWithPlayers(
            classDay,
            yearProvider.currentYear()
        )
    }
}