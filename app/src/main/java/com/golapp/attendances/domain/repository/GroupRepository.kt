package com.golapp.attendances.domain.repository

import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.models.GroupWithClassPlayers
import com.golapp.attendances.domain.models.GroupWithPlayers
import com.golapp.attendances.domain.models.Statistics
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun insert(groupWithClassPlayers: GroupWithClassPlayers)
    suspend fun getGroupsWithClassDaysOnMonth(month: Int): List<GroupWithClassDays>
    suspend fun getGroupWithClassDaysById(groupId: Int): GroupWithClassDays
    suspend fun fetchAllGroups(): Flow<List<GroupWithClassPlayers>>
    suspend fun fetchGroup(groupId: Int): Flow<GroupWithClassPlayers>
    suspend fun deleteGroups()
    suspend fun fetchStatistics(): Flow<List<Statistics>>
    suspend fun getGroupWhitPlayersById(groupId: Int): GroupWithPlayers
}
