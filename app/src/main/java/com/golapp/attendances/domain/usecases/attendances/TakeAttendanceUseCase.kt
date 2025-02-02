package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.domain.models.Attendance

interface TakeAttendanceUseCase {
    suspend operator fun invoke(attendance: Attendance)
}
