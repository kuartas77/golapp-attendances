package com.golapp.attendances.domain.usecases.attendances

import com.golapp.attendances.domain.models.ClassDay

interface GetClassDayByIdUseCase {
    suspend operator fun invoke(classDayId: String): ClassDay
}
