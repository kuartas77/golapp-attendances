package com.golapp.attendances.data.local.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.golapp.attendances.data.local.database.entities.GroupEntity
import com.golapp.attendances.data.local.database.entities.GroupWithClassDaysEntity
import com.golapp.attendances.data.local.database.entities.GroupWithPlayersEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM `groups` ORDER BY name ASC")
    fun observeGroups(): Flow<List<GroupEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM `groups`
        WHERE id IN (
            SELECT DISTINCT group_id
            FROM class_days
            WHERE month = :month
        )
        ORDER BY name ASC
    """
    )
    fun observeGroupsWithClassDaysOnMonth(month: Int): Flow<List<GroupWithClassDaysEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM `groups`
        WHERE id IN (
            SELECT DISTINCT group_id
            FROM class_days
            WHERE month = :month
        )
        ORDER BY name ASC
    """
    )
    suspend fun getGroupsWithClassDaysOnMonth(month: Int): List<GroupWithClassDaysEntity>

    @Transaction
    @Query("SELECT * FROM `groups` WHERE id = :groupId LIMIT 1")
    suspend fun getGroupWithPlayersById(groupId: Int): GroupWithPlayersEntity?

    @Upsert
    suspend fun upsertGroups(groups: List<GroupEntity>)

    @Query("DELETE FROM `groups` WHERE id NOT IN (:ids)")
    suspend fun deleteGroupsNotIn(ids: List<Int>)

    @Query("DELETE FROM `groups`")
    suspend fun deleteAllGroups()
}