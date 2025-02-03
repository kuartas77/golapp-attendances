package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.SyncGroupsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncGroupsUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository
) : SyncGroupsUseCase {
    override suspend fun invoke(): Flow<List<GroupWithClassDays>> = groupRepository.fetchGroupWithClassDaysList()
}
