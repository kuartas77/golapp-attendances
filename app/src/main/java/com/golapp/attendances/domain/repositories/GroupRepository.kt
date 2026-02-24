package com.golapp.attendances.domain.repositories

import com.golapp.attendances.domain.models.ClassDay
import com.golapp.attendances.domain.models.Group
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.models.GroupWithClassPlayers
import com.golapp.attendances.domain.models.GroupWithPlayers
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun syncAssignedGroups()
    fun observeGroups(): Flow<List<Group>>
    fun observeClassDaysByGroup(groupId: Int): Flow<List<ClassDay>>
    suspend fun getGroupWhitPlayersById(groupId: Int): GroupWithPlayers
    fun observeGroupsWithClassDaysOnMonth(month: Int): Flow<List<GroupWithClassDays>>

    // Sync
    suspend fun fetchAllGroupsSnapshot(): List<GroupWithClassPlayers>
    suspend fun upsertGroupsWithClassPlayers(items: List<GroupWithClassPlayers>)
    suspend fun syncGroupsIfEmpty(month: Int)
}