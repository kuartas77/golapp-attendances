package com.golapp.attendances.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.golapp.attendances.data.local.dao.AttendanceDao
import com.golapp.attendances.data.local.dao.ClassDayDao
import com.golapp.attendances.data.local.dao.GroupDao
import com.golapp.attendances.data.local.dao.PlayerDao
import com.golapp.attendances.data.local.models.AttendanceEntity
import com.golapp.attendances.data.local.models.AttendanceSyncEntity
import com.golapp.attendances.data.local.models.ClassDayEntity
import com.golapp.attendances.data.local.models.GroupEntity
import com.golapp.attendances.data.local.models.PlayerEntity

/**
 * [AttendancesDB] is the Room Database for the attendance application.
 *
 * This class defines the database schema and provides access to the Data Access Objects (DAOs)
 * for interacting with the database tables.
 *
 * @property attendanceDao Provides access to the [AttendanceDao] for managing attendance records.
 * @property classDayDao Provides access to the [ClassDayDao] for managing class day information.
 * @property groupDao Provides access to the [GroupDao] for managing group data.
 * @property playerDao Provides access to the [PlayerDao] for managing player information.
 *
 * @constructor Creates an instance of the AttendancesDB. This should be done via Room.databaseBuilder
 *
 * @see RoomDatabase
 * @see AttendanceDao
 * @see ClassDayDao
 * @see GroupDao
 * @see PlayerDao
 * @see GroupEntity
 * @see AttendanceEntity
 * @see AttendanceSyncEntity
 * @see PlayerEntity
 * @see ClassDayEntity
 */
@Database(
    entities = [
        GroupEntity::class,
        AttendanceEntity::class,
        AttendanceSyncEntity::class,
        PlayerEntity::class,
        ClassDayEntity::class
    ],
    version = 2,
    exportSchema = true
)

abstract class AttendancesDB : RoomDatabase() {
    abstract fun attendanceDao(): AttendanceDao
    abstract fun classDayDao(): ClassDayDao
    abstract fun groupDao(): GroupDao
    abstract fun playerDao(): PlayerDao
}
