package com.golapp.attendances.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendances",
    indices = [
        Index(
            value = ["school_id", "group_id", "inscription_id", "player_id", "month", "column"],
            unique = true
        ),
        Index(value = ["group_id"]),
        Index(value = ["player_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "attendance_id")
    val attendanceId: Int? = null,
    @ColumnInfo(name = "school_id")
    val schoolId: Int,
    @ColumnInfo(name = "group_id")
    val trainingGroupId: Int,
    @ColumnInfo(name = "inscription_id")
    val inscriptionId: Int,
    val year: Int,
    val month: Int,
    val column: String,
    val value: String?,
    @ColumnInfo(name = "player_id")
    val playerId: Int
)
