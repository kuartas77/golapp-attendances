package com.golapp.attendances.data.local.models

data class GroupWithClassDaysEntity(
    @androidx.room.Embedded val group: GroupEntity,
    @androidx.room.Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val classDays: List<ClassDayEntity>
)
