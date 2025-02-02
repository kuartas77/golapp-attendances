package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.domain.models.AttendanceWithPlayer
import com.golapp.attendances.domain.models.ClassDay
import kotlinx.coroutines.flow.Flow

interface GetAttendancesByClassDayUseCase {
    suspend operator fun invoke(classDay: ClassDay): Flow<List<AttendanceWithPlayer>>
}
