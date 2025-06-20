package com.golapp.attendances.data.repository

import com.golapp.attendances.common.di.IoDispatcher
import com.golapp.attendances.common.resultOf
import com.golapp.attendances.data.local.datasources.ClassDayLocalDataSource
import com.golapp.attendances.data.local.datasources.GroupsLocalDataSource
import com.golapp.attendances.data.local.datasources.PlayersLocalDataSource
import com.golapp.attendances.data.local.models.GroupWithClassPlayersEntity
import com.golapp.attendances.data.mappers.asDomain
import com.golapp.attendances.data.remote.datasources.GroupsRemoteDataSource
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.models.GroupWithPlayers
import com.golapp.attendances.domain.models.Statistics
import com.golapp.attendances.domain.repository.GroupRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupsLocalDataSource: GroupsLocalDataSource,
    private val groupsRemoteDataSource: GroupsRemoteDataSource,
    private val playerLocalDataSource: PlayersLocalDataSource,
    private val classDayLocalDataSource: ClassDayLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GroupRepository {
    override suspend fun syncGroups() {
        groupsRemoteDataSource.fetchGroups()
            .flowOn(ioDispatcher)
            .collect { groupsWithClassPlayers ->
                if (groupsWithClassPlayers.isNotEmpty()) {
                    groupsWithClassPlayers.forEach { groupWithClassPlayers ->
                        insert(groupWithClassPlayers)
                    }
                }
            }
    }

    override suspend fun fetchGroupWithClassDaysList(): Flow<List<GroupWithClassDays>> = flow {
        resultOf {
            emit(groupsLocalDataSource.getGroupsWithClassDays())
        }.onFailure { emit(emptyList()) }
    }.onStart { emptyList<GroupWithClassDays>() }.flowOn(ioDispatcher)

    override suspend fun fetchGroupsWithClassDaysOnMonth(month: Int): Flow<List<GroupWithClassDays>> =
        flow {
            resultOf {
                val localGroups = groupsLocalDataSource.getGroupsWithClassDaysOnMonth(month)
                if (localGroups.isEmpty()) {

                    groupsRemoteDataSource.fetchGroups()
                        .flowOn(ioDispatcher)
                        .collect { groupsWithClassPlayers ->
                            groupsWithClassPlayers.forEach {
                                insert(it)
                            }
                        }

                    emit(groupsLocalDataSource.getGroupsWithClassDaysOnMonth(month))

                } else {
                    emit(localGroups)
                }
            }.onFailure {
                emit(emptyList<GroupWithClassDays>())
            }
        }.onStart { emptyList<GroupWithClassDays>() }.flowOn(ioDispatcher)

    override suspend fun fetchGroupWithClassDaysById(groupId: Int): Flow<GroupWithClassDays> =
        flow {
            groupsRemoteDataSource.fetchGroup(groupId)
                .flowOn(ioDispatcher)
                .collect {
                    insert(it)
                }

            emit(groupsLocalDataSource.getGroupWithClassDaysById(groupId))

        }.flowOn(ioDispatcher)

    override suspend fun fetchGroupWithPlayers(groupId: Int): Flow<GroupWithPlayers> = flow {
        groupsRemoteDataSource.fetchGroup(groupId)
            .flowOn(ioDispatcher)
            .collect {
                insert(it)
            }

        emit(groupsLocalDataSource.getGroupWhitPlayersById(groupId))
    }.flowOn(ioDispatcher)

    override suspend fun deleteGroups() {
        groupsLocalDataSource.deleteGroups()
    }

    override suspend fun fetchStatistics(): Flow<List<Statistics>> = flow {
        groupsRemoteDataSource.fetchStatistics()
            .flowOn(ioDispatcher)
            .collect {
                if (it.isNotEmpty()) {
                    emit(it.map { entity -> entity.asDomain() })
                }
            }
    }.flowOn(ioDispatcher)

    private suspend fun insert(groupWithClassPlayers: GroupWithClassPlayersEntity) {
        groupsLocalDataSource.deleteGroupById(groupWithClassPlayers.group.id)
        groupsLocalDataSource.insertGroup(groupWithClassPlayers.group)
        if (groupWithClassPlayers.classDays.isNotEmpty()) {
            classDayLocalDataSource.deleteClassDaysByGroupId(groupWithClassPlayers.group.id)
            classDayLocalDataSource.insertClassDayList(groupWithClassPlayers.classDays)
        }
        if (groupWithClassPlayers.players.isNotEmpty()) {
            playerLocalDataSource.deletePlayersByGroupId(groupWithClassPlayers.group.id)
            playerLocalDataSource.insertPlayerList(groupWithClassPlayers.players)
        }
    }
}
