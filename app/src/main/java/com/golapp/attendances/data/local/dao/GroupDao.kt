package com.golapp.attendances.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.golapp.attendances.data.local.models.ClassDayEntity
import com.golapp.attendances.data.local.models.GroupEntity
import com.golapp.attendances.data.local.models.GroupWithClassDaysEntity
import com.golapp.attendances.data.local.models.GroupWithPlayersEntity


@Dao
interface GroupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(groupEntity: GroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupList(groupList: List<GroupEntity>)

    @Transaction
    @Query("SELECT * FROM groups WHERE id = :groupId")
    suspend fun getGroupWithClassDaysById(groupId: Int): GroupWithClassDaysEntity

    @Transaction
    @Query("SELECT * FROM groups")
    suspend fun getGroupsWithClassDays(): List<GroupWithClassDaysEntity>

    @Transaction
    @Query("SELECT * FROM groups WHERE id = :groupId")
    suspend fun getGroupWhitPlayersById(groupId: Int): GroupWithPlayersEntity

    @Transaction
    @Query("SELECT * FROM groups JOIN class_days ON groups.id = class_days.group_id WHERE month = :month")
    suspend fun getGroupsWithClassDaysOnMonth(month: Int): Map<GroupEntity, List<ClassDayEntity>>

    @Query("DELETE FROM groups")
    suspend fun deleteGroups()

    @Query("DELETE FROM groups WHERE id = :groupId")
    suspend fun deleteGroupById(groupId: Int)
}
