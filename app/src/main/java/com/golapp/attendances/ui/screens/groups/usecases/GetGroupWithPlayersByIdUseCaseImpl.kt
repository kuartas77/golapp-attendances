package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.domain.models.GroupWithPlayers
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetGroupWithPlayersByIdUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetGroupWithPlayersByIdUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GetGroupWithPlayersByIdUseCase {

    override suspend fun invoke(groupId: Int): GroupWithPlayers = withContext(ioDispatcher) {
        val remoteGroup = groupRepository.fetchGroup(groupId).first()
        groupRepository.insert(remoteGroup)
        groupRepository.getGroupWhitPlayersById(groupId)
    }
}
