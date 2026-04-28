package com.golapp.attendances.data.local.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.golapp.attendances.data.local.database.entities.AttendanceEntity
import com.golapp.attendances.data.local.database.entities.AttendanceSyncEntity
import com.golapp.attendances.data.local.database.entities.AttendanceWithPlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Transaction
    @Query(
        """
        SELECT * FROM attendances
        WHERE school_id = :schoolId
          AND group_id  = :groupId
          AND year      = :year
          AND month     = :month
          AND `column`  = :column
        ORDER BY
            (
                SELECT CAST(REPLACE(players.category, 'SUB-', '') AS INTEGER)
                FROM players
                WHERE players.player_id = attendances.player_id
            ) ASC
    """
    )
    fun observeAttendancesWithPlayers(
        schoolId: Int,
        groupId: Int,
        year: Int,
        month: Int,
        column: String
    ): Flow<List<AttendanceWithPlayerEntity>>

    @Transaction
    @Query(
        """
        SELECT * FROM attendances
        WHERE school_id = :schoolId
          AND group_id  = :groupId
          AND year      = :year
          AND month     = :month
          AND `column`  = :column
        ORDER BY
            (
                SELECT CAST(REPLACE(players.category, 'SUB-', '') AS INTEGER)
                FROM players
                WHERE players.player_id = attendances.player_id
            ) ASC,
            (
                SELECT players.full_names
                FROM players
                WHERE players.player_id = attendances.player_id
            ) COLLATE NOCASE ASC
    """
    )
    suspend fun getAttendancesWithPlayers(
        schoolId: Int,
        groupId: Int,
        year: Int,
        month: Int,
        column: String
    ): List<AttendanceWithPlayerEntity>

    @Query(
        """
        SELECT * FROM attendances
        WHERE id = :id
    """
    )
    suspend fun getAttendanceById(id: Long): AttendanceEntity?


    @Query("SELECT * FROM attendances")
    suspend fun getAllAttendances(): List<AttendanceEntity>

    @Query("SELECT * FROM attendance_sync")
    suspend fun getAllAttendanceSync(): List<AttendanceSyncEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendancesSync(items: List<AttendanceSyncEntity>)

    @Delete
    suspend fun deleteAttendanceSync(item: AttendanceSyncEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnore(items: List<AttendanceEntity>): List<Long>

    @Query(
        """
        UPDATE attendances SET
            attendance_id = :attendanceId,
            value = :value
        WHERE school_id = :schoolId
          AND group_id  = :groupId
          AND inscription_id = :inscriptionId
          AND player_id = :playerId
          AND year      = :year
          AND month     = :month
          AND `column`  = :column
    """
    )
    suspend fun updateByNaturalKey(
        attendanceId: Int?,
        value: String?,
        schoolId: Int,
        groupId: Int,
        inscriptionId: Int,
        playerId: Int,
        year: Int,
        month: Int,
        column: String
    )

    @Query("UPDATE attendances SET value = :value WHERE id = :id")
    suspend fun updateValueById(id: Long, value: String?)
}
