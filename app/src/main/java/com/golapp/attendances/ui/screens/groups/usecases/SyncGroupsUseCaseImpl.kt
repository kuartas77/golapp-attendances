package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.SyncGroupsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class SyncGroupsUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : SyncGroupsUseCase {
    override suspend fun invoke() {
        val groupsWithClassPlayers = groupRepository.fetchAllGroups().flowOn(ioDispatcher).first()
        groupsWithClassPlayers.forEach(groupRepository::insert)
    }
}
