package com.co.golapp.attendances.data.datasources.local

import com.co.golapp.attendances.data.local.dao.GroupDao
import com.co.golapp.attendances.data.local.datasources.GroupsLocalDataSource
import com.co.golapp.attendances.data.local.models.GroupEntity
import com.co.golapp.attendances.data.mappers.asDomain
import com.co.golapp.attendances.domain.models.GroupWithClassDays
import com.co.golapp.attendances.domain.models.GroupWithPlayers
import javax.inject.Inject

class GroupsLocalDataSourceImpl @Inject constructor(
    private val groupDao: GroupDao
) : GroupsLocalDataSource {
    override suspend fun insertGroup(group: GroupEntity) = groupDao.insertGroup(group)


    override suspend fun insertGroupList(groupList: List<GroupEntity>) =
        groupDao.insertGroupList(groupList)


    override suspend fun getGroupsWithClassDays(): List<GroupWithClassDays> =
        groupDao.getGroupsWithClassDays().asDomain()


    override suspend fun getGroupsWithClassDaysOnMonth(month: Int): List<GroupWithClassDays> =
        groupDao.getGroupsWithClassDaysOnMonth(month).asDomain()


    override suspend fun getGroupWithClassDaysById(groupId: Int): GroupWithClassDays =
        groupDao.getGroupWithClassDaysById(groupId).asDomain()


    override suspend fun getGroupWhitPlayersById(groupId: Int): GroupWithPlayers =
        groupDao.getGroupWhitPlayersById(groupId).asDomain()


    override suspend fun deleteGroups() = groupDao.deleteGroups()
}
