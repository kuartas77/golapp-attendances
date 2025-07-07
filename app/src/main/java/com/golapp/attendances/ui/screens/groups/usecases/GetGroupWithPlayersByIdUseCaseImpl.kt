package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.domain.models.GroupWithPlayers
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetGroupWithPlayersByIdUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetGroupWithPlayersByIdUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GetGroupWithPlayersByIdUseCase {
    override suspend fun invoke(groupId: Int): Flow<GroupWithPlayers> = flow {
        groupRepository.fetchGroup(groupId).flowOn(ioDispatcher).collect {
            groupRepository.insert(it)
        }

        emit(groupRepository.getGroupWhitPlayersById(groupId))
    }.flowOn(ioDispatcher)
}
