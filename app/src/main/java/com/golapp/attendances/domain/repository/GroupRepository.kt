package com.golapp.attendances.domain.repository

import com.golapp.attendances.data.local.models.GroupWithClassPlayersEntity
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.models.GroupWithPlayers
import com.golapp.attendances.domain.models.Statistics
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun insert(groupWithClassPlayers: GroupWithClassPlayersEntity)
    suspend fun getGroupsWithClassDaysOnMonth(month: Int): List<GroupWithClassDays>
    suspend fun getGroupWithClassDaysById(groupId: Int): GroupWithClassDays
    suspend fun fetchAllGroups(): Flow<List<GroupWithClassPlayersEntity>>
    suspend fun fetchGroup(groupId: Int): Flow<GroupWithClassPlayersEntity>
    suspend fun deleteGroups()
    suspend fun fetchStatistics(): Flow<List<Statistics>>
    suspend fun getGroupWhitPlayersById(groupId: Int): GroupWithPlayers
}
