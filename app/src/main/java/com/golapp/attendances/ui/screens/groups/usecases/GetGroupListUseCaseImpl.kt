package com.golapp.attendances.ui.screens.groups.usecases

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.repository.GroupRepository
import com.golapp.attendances.domain.usecases.groups.GetGroupListUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class GetGroupListUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GetGroupListUseCase {

    override suspend fun invoke(month: Int): Flow<List<GroupWithClassDays>> = flow {
        // 1. Intentar obtener datos locales primero
        val localGroups = groupRepository.getGroupsWithClassDaysOnMonth(month)

        if (localGroups.isEmpty()) {
            Timber.d("Fetching groups from remote because local is empty")

            // 2. Obtener del remoto, tomamos el primer valor (snapshot) para no bloquearnos
            val remoteGroups = groupRepository.fetchAllGroups().first()

            // 3. Insertar cada grupo en la DB local
            remoteGroups.forEach { groupWithClassPlayers ->
                Timber.d("Inserting group: ${groupWithClassPlayers.group.name}")
                groupRepository.insert(groupWithClassPlayers)
            }

            // 4. Emitir el resultado actualizado desde la DB local
            emit(groupRepository.getGroupsWithClassDaysOnMonth(month))
        } else {
            Timber.d("Emitting local groups")
            emit(localGroups)
        }
    }
        .catch { e ->
            Timber.e(e, "Error sync groups")
            emit(emptyList())
        }
        .flowOn(ioDispatcher)
}
