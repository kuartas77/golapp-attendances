package com.golapp.attendances.data.local.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.golapp.attendances.data.local.database.entities.ClassDayEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ClassDayDao {
    @Query(
        """
        SELECT * FROM class_days
        WHERE group_id = :groupId
        ORDER BY date ASC
    """
    )
    fun observeClassDaysByGroup(groupId: Int): Flow<List<ClassDayEntity>>

    @Query("SELECT * FROM class_days WHERE class_day_id = :classDayId")
    suspend fun getClassDayById(classDayId: String): ClassDayEntity

    @Upsert
    suspend fun upsertClassDays(classDays: List<ClassDayEntity>)
}