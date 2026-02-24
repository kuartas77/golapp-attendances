package com.golapp.attendances.domain.usecases.groups

import com.golapp.attendances.domain.repositories.GroupRepository
import javax.inject.Inject

class SyncAssignedGroupsUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke() {
        groupRepository.syncAssignedGroups()
    }
}