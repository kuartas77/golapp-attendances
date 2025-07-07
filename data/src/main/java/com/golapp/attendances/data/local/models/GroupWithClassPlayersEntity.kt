package com.golapp.attendances.data.local.models

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
        parentColumn = "class_day_id",
        entityColumn = "group_id"
    )
    val classDays: List<ClassDayEntity>
)
