package com.golapp.attendances.data.local.models

import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@androidx.room.Entity(
    tableName = "class_days",
    indices = [Index("group_id")],
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
data class ClassDayEntity(
    @PrimaryKey
    @ColumnInfo(name = "class_day_id")
    val classDayId: String,
    val date: Int,
    val day: String,
    val month: Int,
    @ColumnInfo(name = "month_name")
    val monthName: String,
    val column: String,
    @ColumnInfo(name = "group_id")
    val groupId: Int,
    @ColumnInfo(name = "school_id")
    val schoolId: Int
)
