package com.golapp.attendances.data.local.models

@androidx.room.Entity(tableName = "groups")
data class GroupEntity(
    @androidx.room.PrimaryKey
    val id: Int,
    val name: String,
    val days: String,
    @androidx.room.ColumnInfo(name = "explode_schedules")
    val explodeSchedules: String,
    @androidx.room.ColumnInfo(name = "full_schedule_group")
    val fullScheduleGroup: String,
    @androidx.room.ColumnInfo(name = "player_count")
    val playerCount: Int,
    @androidx.room.ColumnInfo(name = "full_group")
    val fullGroup: String
)
