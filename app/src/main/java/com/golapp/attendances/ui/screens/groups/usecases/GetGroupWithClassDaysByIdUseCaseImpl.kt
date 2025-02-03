package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetGroupWithClassDaysByIdUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupWithClassDaysByIdUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository
) : GetGroupWithClassDaysByIdUseCase {
    override suspend fun invoke(groupId: Int): Flow<GroupWithClassDays> =
        groupRepository.fetchGroupWithClassDaysById(groupId)
}
