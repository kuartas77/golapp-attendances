package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetGroupListUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetGroupListUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GetGroupListUseCase {

    override suspend fun invoke(month: Int): List<GroupWithClassDays> = withContext(ioDispatcher) {
        val localGroups = groupRepository.getGroupsWithClassDaysOnMonth(month)
        if (localGroups.isNotEmpty()) {
            return@withContext localGroups
        }

        runCatching {
            val remoteGroups = groupRepository.fetchAllGroups().first()
            remoteGroups.forEach(groupRepository::insert)
            groupRepository.getGroupsWithClassDaysOnMonth(month)
        }.getOrDefault(emptyList())
    }
}
