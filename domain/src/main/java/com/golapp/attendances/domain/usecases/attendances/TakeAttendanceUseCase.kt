package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.domain.models.AttendanceWithPlayer

interface TakeAttendanceUseCase {
    suspend operator fun invoke(attendanceWithPlayer: AttendanceWithPlayer)
}
