package com.golapp.attendances.domain.repository

import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.models.GroupWithPlayers
import com.golapp.attendances.domain.models.Statistics
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun syncGroups()
    suspend fun fetchGroupWithClassDaysList(): Flow<List<GroupWithClassDays>>
    suspend fun fetchGroupsWithClassDaysOnMonth(month: Int): Flow<List<GroupWithClassDays>>
    suspend fun fetchGroupWithClassDaysById(groupId: Int): Flow<GroupWithClassDays>
    suspend fun fetchGroupWithPlayers(groupId: Int): Flow<GroupWithPlayers>
    suspend fun deleteGroups()
    suspend fun fetchStatistics(): Flow<List<Statistics>>
}
