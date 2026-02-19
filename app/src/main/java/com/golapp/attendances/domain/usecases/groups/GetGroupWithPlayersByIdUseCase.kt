package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.domain.models.GroupWithPlayers

interface GetGroupWithPlayersByIdUseCase {
    suspend operator fun invoke(groupId: Int): GroupWithPlayers
}
