package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.data.util.resultOf
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetGroupListUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetGroupListUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GetGroupListUseCase {
    override suspend fun invoke(month: Int): Flow<List<GroupWithClassDays>> = flow {
        resultOf {
            val localGroups = groupRepository.getGroupsWithClassDaysOnMonth(month)
            if (localGroups.isEmpty()) {

                groupRepository.fetchAllGroups().flowOn(ioDispatcher)
                    .collect { groupsWithClassPlayers ->
                        groupsWithClassPlayers.forEach { groupWithClassPlayers ->
                            groupRepository.insert(groupWithClassPlayers)
                        }
                    }

                emit(groupRepository.getGroupsWithClassDaysOnMonth(month))

            } else {
                emit(localGroups)
            }
        }.onFailure {
            emit(emptyList())
        }
    }.onStart { emptyList<GroupWithClassDays>() }.flowOn(ioDispatcher)
}
