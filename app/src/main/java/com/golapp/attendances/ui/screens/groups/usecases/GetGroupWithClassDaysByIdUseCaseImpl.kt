package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetGroupWithClassDaysByIdUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetGroupWithClassDaysByIdUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GetGroupWithClassDaysByIdUseCase {
    override suspend fun invoke(groupId: Int): Flow<GroupWithClassDays> = flow {
        groupRepository.fetchGroup(groupId).flowOn(ioDispatcher).collect {
            groupRepository.insert(it)
        }

        emit(groupRepository.getGroupWithClassDaysById(groupId))

    }.flowOn(ioDispatcher)
}
