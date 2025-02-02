package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.domain.models.ClassDay

interface VerifyAttendancesByClassIdUseCase {
    suspend operator fun invoke(classDay: ClassDay)
}
