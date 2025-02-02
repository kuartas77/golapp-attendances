package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.domain.models.GroupWithPlayers
import kotlinx.coroutines.flow.Flow

interface GetGroupWithPlayersByIdUseCase {
    suspend operator fun invoke(groupId: Int): Flow<GroupWithPlayers>
}
