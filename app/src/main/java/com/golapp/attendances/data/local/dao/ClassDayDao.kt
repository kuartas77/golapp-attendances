package com.golapp.attendances.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.golapp.attendances.data.local.models.ClassDayEntity


@Dao
interface ClassDayDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassDay(classDaysEntity: ClassDayEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClassDayList(classDayList: List<ClassDayEntity>)

    @Query("SELECT * FROM class_days WHERE class_day_id = :classDayId")
    suspend fun getClassDayById(classDayId: String): ClassDayEntity

    @Query("DELETE FROM class_days")
    suspend fun deleteClassDays()

    @Query("DELETE FROM class_days WHERE group_id = :groupId")
    suspend fun deleteClassDaysByGroupId(groupId: Int)
}
