package com.golapp.attendances.data.local.database.entities

data class GroupWithClassPlayersEntity(
    @androidx.room.Embedded val group: GroupEntity,
    @androidx.room.Relation(
        entity = PlayerEntity::class,
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val players: List<PlayerEntity>,
    @androidx.room.Relation(
        entity = ClassDayEntity::class,
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val classDays: List<ClassDayEntity>
)