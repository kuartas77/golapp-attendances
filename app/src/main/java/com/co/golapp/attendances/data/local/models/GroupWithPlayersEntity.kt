package com.co.golapp.attendances.data.local.models

data class GroupWithPlayersEntity(
    @androidx.room.Embedded val group: GroupEntity,
    @androidx.room.Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val players: List<PlayerEntity>
)
