package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.domain.models.GroupWithPlayers
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetGroupWithPlayersByIdUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupWithPlayersByIdUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository
) : GetGroupWithPlayersByIdUseCase {
    override suspend fun invoke(groupId: Int): Flow<GroupWithPlayers> =
        groupRepository.fetchGroupWithPlayers(groupId)
}
