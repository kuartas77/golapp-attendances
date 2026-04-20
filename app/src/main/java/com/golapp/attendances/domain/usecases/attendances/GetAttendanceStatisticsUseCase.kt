package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.data.remote.models.dtos.StatisticsDto
import com.golapp.attendances.domain.repositories.AttendanceRepository
import javax.inject.Inject

class GetAttendanceStatisticsUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) {
    suspend operator fun invoke(): List<StatisticsDto> =
        attendanceRepository.getAttendanceStatistics()
}
