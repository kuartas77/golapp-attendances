package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.core.di.IoDispatcher
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.repositories.GroupRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ObserveGroupsWithClassDaysOnMonthUseCase @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    operator fun invoke(month: Int): Flow<List<GroupWithClassDays>> =
        groupRepository.observeGroupsWithClassDaysOnMonth(month)
            .onStart { groupRepository.syncGroupsIfEmpty(month) }
            .flowOn(ioDispatcher)
}