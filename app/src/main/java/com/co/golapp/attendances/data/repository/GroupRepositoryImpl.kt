package com.co.golapp.attendances.data.repository

import com.co.golapp.attendances.common.di.IoDispatcher
import com.co.golapp.attendances.common.resultOf
import com.co.golapp.attendances.data.local.datasources.AttendancesLocalDataSource
import com.co.golapp.attendances.data.local.datasources.ClassDayLocalDataSource
import com.co.golapp.attendances.data.local.datasources.GroupsLocalDataSource
import com.co.golapp.attendances.data.local.datasources.PlayersLocalDataSource
import com.co.golapp.attendances.data.local.models.GroupWithClassPlayersEntity
import com.co.golapp.attendances.data.remote.datasources.GroupsRemoteDataSource
import com.co.golapp.attendances.domain.models.GroupWithClassDays
import com.co.golapp.attendances.domain.models.GroupWithPlayers
import com.co.golapp.attendances.domain.repository.GroupRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupsLocalDataSource: GroupsLocalDataSource,
    private val groupsRemoteDataSource: GroupsRemoteDataSource,
    private val attendanceLocalDataSource: AttendancesLocalDataSource,
    private val playerLocalDataSource: PlayersLocalDataSource,
    private val classDayLocalDataSource: ClassDayLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GroupRepository {
    override suspend fun fetchGroupWithClassDaysList(): Flow<List<GroupWithClassDays>> = flow {
        resultOf {
            groupsLocalDataSource.deleteGroups()
            classDayLocalDataSource.deleteClassDays()
            playerLocalDataSource.deletePlayers()
            attendanceLocalDataSource.deleteAttendances()

            groupsRemoteDataSource.fetchGroups().collect { groupsWithClassPlayers ->
                groupsWithClassPlayers.forEach { groupWithClassPlayers ->
                    insert(groupWithClassPlayers)
                }
            }
            emit(groupsLocalDataSource.getGroupsWithClassDays())

        }.onFailure { emit(emptyList()) }
    }.onStart { emptyList<GroupWithClassDays>() }.flowOn(ioDispatcher)

    override suspend fun fetchGroupsWithClassDaysOnMonth(month: Int): Flow<List<GroupWithClassDays>> =
        flow {
            resultOf {
                val localGroups = groupsLocalDataSource.getGroupsWithClassDaysOnMonth(month)
                if (localGroups.isEmpty()) {
                    groupsRemoteDataSource.fetchGroups().collect { groupsWithClassPlayers ->
                        groupsWithClassPlayers.forEach { groupWithClassPlayers ->
                            insert(groupWithClassPlayers)
                        }
                        emit(groupsLocalDataSource.getGroupsWithClassDaysOnMonth(month))
                    }
                } else {
                    emit(localGroups)
                }
            }.onFailure {
                emit(emptyList<GroupWithClassDays>())
            }
        }.onStart { emptyList<GroupWithClassDays>() }.flowOn(ioDispatcher)

    override suspend fun fetchGroupWithClassDaysById(groupId: Int): Flow<GroupWithClassDays> =
        flow {
            val localGroup = groupsLocalDataSource.getGroupWithClassDaysById(groupId)

            emit(localGroup)

        }.flowOn(ioDispatcher)

    override suspend fun fetchGroupWithPlayers(groupId: Int): Flow<GroupWithPlayers> {
        TODO("Not yet implemented")
    }

    private suspend fun insert(groupWithClassPlayers: GroupWithClassPlayersEntity) {
        groupsLocalDataSource.insertGroup(groupWithClassPlayers.group)
        classDayLocalDataSource.insertClassDayList(groupWithClassPlayers.classDays)
        if (groupWithClassPlayers.players.isNotEmpty()) {
            playerLocalDataSource.insertPlayerList(groupWithClassPlayers.players)
        }
    }
}
