package com.golapp.attendances.data.local.datasources

import com.golapp.attendances.data.local.models.GroupEntity
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.models.GroupWithPlayers

interface GroupsLocalDataSource {
    suspend fun insertGroup(group: GroupEntity)
    suspend fun insertGroupList(groupList: List<GroupEntity>)
    suspend fun getGroupsWithClassDays(): List<GroupWithClassDays>
    suspend fun getGroupsWithClassDaysOnMonth(month: Int): List<GroupWithClassDays>
    suspend fun getGroupWithClassDaysById(groupId: Int): GroupWithClassDays
    suspend fun getGroupWhitPlayersById(groupId: Int): GroupWithPlayers
    suspend fun deleteGroups()
    suspend fun deleteGroupById(groupId: Int)
}
