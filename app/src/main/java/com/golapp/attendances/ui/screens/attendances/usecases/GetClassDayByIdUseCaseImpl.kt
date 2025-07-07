package com.golapp.attendances.ui.screens.attendances.usecases

import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.repository.ClassDayRepository
import com.golapp.attendances.domain.usecases.attendances.GetClassDayByIdUseCase
import javax.inject.Inject

class GetClassDayByIdUseCaseImpl @Inject constructor(
    private val classDayRepository: ClassDayRepository
) : GetClassDayByIdUseCase {
    override suspend fun invoke(classDayId: String): ClassDay =
        classDayRepository.getClassDayById(classDayId)
}
