package com.golapp.attendances.data.remote.datasources

import com.golapp.attendances.data.local.models.GroupWithClassPlayersEntity
import com.golapp.attendances.data.local.models.StatisticsEntity
import kotlinx.coroutines.flow.Flow

interface GroupsRemoteDataSource {
    suspend fun fetchGroups(): Flow<List<GroupWithClassPlayersEntity>>
    suspend fun fetchGroup(groupId: Int): Flow<GroupWithClassPlayersEntity>
    suspend fun fetchStatistics(): Flow<List<StatisticsEntity>>
}
