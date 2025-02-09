package com.golapp.attendances.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.golapp.attendances.data.local.models.AttendanceEntity
import com.golapp.attendances.data.local.models.AttendanceSyncEntity
import com.golapp.attendances.data.local.models.AttendanceWithPlayerEntity

import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendanceEntity: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendanceList: List<AttendanceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceSync(attendanceSyncEntity: AttendanceSyncEntity)

    @Transaction
    @Query("SELECT * FROM attendances WHERE id = :attendanceId")
    suspend fun getAttendanceWithPlayerById(attendanceId: Int): AttendanceWithPlayerEntity?

    @Transaction
    @Query("SELECT * FROM attendances WHERE training_group_id = :groupId AND month = :month AND `column` = :column AND school_id = :schoolId")
    suspend fun getAttendancesWithPlayers(
        groupId: Int,
        month: Int,
        column: String,
        schoolId: Int
    ): List<AttendanceWithPlayerEntity>

    @Transaction
    @Query("SELECT * FROM attendances WHERE training_group_id = :groupId AND month = :month AND `column` = :column AND school_id = :schoolId")
    fun getAttendances(
        groupId: Int,
        month: Int,
        column: String,
        schoolId: Int
    ): Flow<List<AttendanceWithPlayerEntity>>

    @Query("SELECT * FROM attendances WHERE id = :id")
    suspend fun getAttendanceById(id: Long): AttendanceEntity

    @Query("SELECT * FROM attendance_sync")
    suspend fun getAttendancesSync(): List<AttendanceSyncEntity>

    @Query("DELETE FROM attendances")
    suspend fun deleteAttendances()

    @Delete
    suspend fun deleteAttendance(attendanceEntity: AttendanceEntity)

    @Delete
    suspend fun deleteAttendanceSync(attendanceSyncEntity: AttendanceSyncEntity)
}
