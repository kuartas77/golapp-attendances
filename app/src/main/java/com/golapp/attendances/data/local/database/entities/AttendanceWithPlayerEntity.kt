package com.golapp.attendances.data.local.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class AttendanceWithPlayerEntity(
    @Embedded val attendance: AttendanceEntity,
    @Relation(
        parentColumn = "player_id",
        entityColumn = "player_id"
    )
    val player: PlayerEntity
)