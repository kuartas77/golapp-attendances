package com.co.golapp.attendances.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_sync")
data class AttendanceSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long
)
