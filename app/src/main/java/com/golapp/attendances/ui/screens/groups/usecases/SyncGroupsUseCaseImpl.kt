package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.SyncGroupsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SyncGroupsUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SyncGroupsUseCase {
    override suspend fun invoke() {
        groupRepository.fetchAllGroups().flowOn(ioDispatcher).collect { groupsWithClassPlayers ->
            if (groupsWithClassPlayers.isNotEmpty()) {
                groupsWithClassPlayers.forEach { groupWithClassPlayers ->
                    groupRepository.insert(groupWithClassPlayers)
                }
            }
        }
    }
}
