package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.repositories.GroupRepository
import com.golapp.attendances.domain.models.ClassDay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ObserveClassDaysByGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(groupId: Int): Flow<List<ClassDay>> =
        groupRepository.observeClassDaysByGroup(groupId)
            .flowOn(ioDispatcher)
}