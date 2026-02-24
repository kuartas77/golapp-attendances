package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.repositories.GroupRepository
import com.golapp.attendances.domain.models.Group
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ObserveGroupsUseCase @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<List<Group>> =
        groupRepository.observeGroups()
            .flowOn(ioDispatcher)
}