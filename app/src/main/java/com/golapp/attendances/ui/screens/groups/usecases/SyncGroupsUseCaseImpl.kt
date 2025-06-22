package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.SyncGroupsUseCase
import javax.inject.Inject

class SyncGroupsUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository
) : SyncGroupsUseCase {
    override suspend fun invoke() = groupRepository.syncGroups()
}
