package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetGroupListUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGroupListUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository
) : GetGroupListUseCase {
    override suspend fun invoke(month: Int): Flow<List<GroupWithClassDays>> =
        groupRepository.fetchGroupsWithClassDaysOnMonth(month)
}
