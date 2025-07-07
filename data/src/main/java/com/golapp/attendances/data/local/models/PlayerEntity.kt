package com.golapp.attendances.data.local.models

import androidx.room.ForeignKey
import androidx.room.Index

@androidx.room.Entity(
    tableName = "players",
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
data class PlayerEntity(
    @androidx.room.PrimaryKey
    @androidx.room.ColumnInfo(name = "player_id")
    val playerId: Int,
    @androidx.room.ColumnInfo(name = "group_id")
    val groupId: Int,
    @androidx.room.ColumnInfo(name = "unique_code")
    val uniqueCode: String,
    val names: String,
    @androidx.room.ColumnInfo(name = "last_names")
    val lastNames: String,
    val category: String,
    @androidx.room.ColumnInfo(name = "full_names")
    val fullNames: String,
    @androidx.room.ColumnInfo(name = "photo_url")
    val photoUrl: String,
    @androidx.room.ColumnInfo(name = "inscription_id")
    val inscriptionId: Int
)
