package com.golapp.attendances.domain.usecases.classDays

import com.golapp.attendances.domain.repositories.ClassDayRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetClassDayByIdUseCase @Inject constructor(
    private val classDayRepository: ClassDayRepository,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(id: String) = withContext(ioDispatcher) {
        classDayRepository.getClassDayById(id)
    }
}