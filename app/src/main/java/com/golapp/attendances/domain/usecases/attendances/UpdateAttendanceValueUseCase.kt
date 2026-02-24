package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.repositories.AttendanceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpdateAttendanceValueUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(localAttendanceId: Long, value: String?) =
        withContext(ioDispatcher) {
            attendanceRepository.updateAttendanceValue(localAttendanceId, value)
        }
}