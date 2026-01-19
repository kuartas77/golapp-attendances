package com.golapp.attendances.data.repository

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.data.local.datasources.ClassDayLocalDataSource
import com.golapp.attendances.data.local.datasources.GroupsLocalDataSource
import com.golapp.attendances.data.local.datasources.PlayersLocalDataSource
import com.golapp.attendances.data.mappers.asDomain
import com.golapp.attendances.data.mappers.asEntity
import com.golapp.attendances.data.remote.datasources.GroupsRemoteDataSource
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.models.GroupWithClassPlayers
import com.golapp.attendances.domain.models.GroupWithPlayers
import com.golapp.attendances.domain.models.Statistics
import com.golapp.attendances.domain.repository.GroupRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val groupsLocalDataSource: GroupsLocalDataSource,
    private val groupsRemoteDataSource: GroupsRemoteDataSource,
    private val playerLocalDataSource: PlayersLocalDataSource,
    private val classDayLocalDataSource: ClassDayLocalDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GroupRepository {

    override suspend fun fetchAllGroups(): Flow<List<GroupWithClassPlayers>> =
        groupsRemoteDataSource.fetchGroups().map {
            it.map { entity ->
                Timber.d("Fetching group: ${entity.group.name}")
                entity.asDomain()
            }
        }

    override suspend fun getGroupsWithClassDaysOnMonth(month: Int): List<GroupWithClassDays> =
        groupsLocalDataSource.getGroupsWithClassDaysOnMonth(month)

    override suspend fun fetchGroup(groupId: Int): Flow<GroupWithClassPlayers> =
        groupsRemoteDataSource.fetchGroup(groupId).map { entity -> entity.asDomain() }

    override suspend fun getGroupWithClassDaysById(groupId: Int): GroupWithClassDays =
        groupsLocalDataSource.getGroupWithClassDaysById(groupId)

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

    override suspend fun getGroupWhitPlayersById(groupId: Int): GroupWithPlayers =
        groupsLocalDataSource.getGroupWhitPlayersById(groupId)


    override suspend fun insert(groupWithClassPlayers: GroupWithClassPlayers) {
        groupsLocalDataSource.deleteGroupById(groupWithClassPlayers.group.id)
        groupsLocalDataSource.insertGroup(groupWithClassPlayers.group.asEntity())
        if (groupWithClassPlayers.classDays.isNotEmpty()) {
            classDayLocalDataSource.deleteClassDaysByGroupId(groupWithClassPlayers.group.id)
            classDayLocalDataSource.insertClassDayList(groupWithClassPlayers.classDays.asEntity())
        }
        if (groupWithClassPlayers.players.isNotEmpty()) {
            playerLocalDataSource.deletePlayersByGroupId(groupWithClassPlayers.group.id)
            playerLocalDataSource.insertPlayerList(groupWithClassPlayers.players.asEntity())
        }
    }
}
