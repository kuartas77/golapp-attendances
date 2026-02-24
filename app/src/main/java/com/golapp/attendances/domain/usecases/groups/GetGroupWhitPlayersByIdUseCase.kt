package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.repositories.GroupRepository
import com.golapp.attendances.domain.models.GroupWithPlayers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetGroupWhitPlayersByIdUseCase @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(groupId: Int): GroupWithPlayers =
        withContext(ioDispatcher) {
            groupRepository.getGroupWhitPlayersById(groupId)
        }
}