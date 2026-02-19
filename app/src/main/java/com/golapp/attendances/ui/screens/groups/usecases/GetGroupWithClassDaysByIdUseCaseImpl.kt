package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetGroupWithClassDaysByIdUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetGroupWithClassDaysByIdUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GetGroupWithClassDaysByIdUseCase {

    override suspend fun invoke(groupId: Int): GroupWithClassDays = withContext(ioDispatcher) {
        val remoteGroup = groupRepository.fetchGroup(groupId).first()
        groupRepository.insert(remoteGroup)
        groupRepository.getGroupWithClassDaysById(groupId)
    }
}
