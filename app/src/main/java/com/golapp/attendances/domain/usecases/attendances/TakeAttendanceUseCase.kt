package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.repositories.AttendanceRepository
import javax.inject.Inject

class TakeAttendanceUseCase @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) {
    suspend operator fun invoke(attendanceWithPlayer: AttendanceWithPlayer) {
        if (attendanceWithPlayer.id !== null && attendanceWithPlayer.value !== null) {
            attendanceRepository.updateAttendanceValue(
                attendanceWithPlayer.id,
                attendanceWithPlayer.value
            )
            return
        }
    }
}