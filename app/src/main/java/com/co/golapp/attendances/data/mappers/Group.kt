package com.co.golapp.attendances.data.mappers

import com.co.golapp.attendances.data.local.models.ClassDayEntity
import com.co.golapp.attendances.data.local.models.GroupEntity
import com.co.golapp.attendances.data.local.models.GroupWithClassDaysEntity
import com.co.golapp.attendances.data.local.models.GroupWithClassPlayersEntity
import com.co.golapp.attendances.data.local.models.GroupWithPlayersEntity
import com.co.golapp.attendances.data.remote.dto.DtoGroup
import com.co.golapp.attendances.data.remote.dto.ResponseGroup
import com.co.golapp.attendances.data.remote.dto.ResponseGroups
import com.co.golapp.attendances.domain.models.Group
import com.co.golapp.attendances.domain.models.GroupWithClassDays
import com.co.golapp.attendances.domain.models.GroupWithClassPlayers
import com.co.golapp.attendances.domain.models.GroupWithPlayers

fun DtoGroup.asEntity(): GroupEntity = GroupEntity(
    id = id,
    name = name,
    days = days,
    explodeSchedules = explodeSchedules,
    fullScheduleGroup = fullScheduleGroup,
    fullGroup = fullGroup,
    playerCount = playerCount
)

@JvmName("ListGroupDtoAsEntity")
fun List<DtoGroup>.asEntity(): List<GroupEntity> = map { it.asEntity() }

fun ResponseGroups.asEntity(): List<GroupWithClassPlayersEntity> = groups.map {
    GroupWithClassPlayersEntity(
        group = it.asEntity(),
        players = it.players.asEntity(),
        classDays = it.classDays.asEntity()
    )
}

fun ResponseGroup.asEntity(): GroupWithClassPlayersEntity = GroupWithClassPlayersEntity(
    group = group.asEntity(),
    players = group.players.asEntity(),
    classDays = group.classDays.asEntity()
)

fun Group.asEntity(): GroupEntity = GroupEntity(
    id = id,
    name = name,
    days = days,
    explodeSchedules = explodeSchedules,
    fullScheduleGroup = fullScheduleGroup,
    fullGroup = fullGroup,
    playerCount = playerCount
)

@JvmName("ListGroupAsEntity")
fun List<Group>.asEntity(): List<GroupEntity> = map { it.asEntity() }

fun GroupWithClassDaysEntity.asDomain(): GroupWithClassDays = GroupWithClassDays(
    id = group.id,
    name = group.name,
    days = group.days,
    explodeSchedules = group.explodeSchedules,
    fullScheduleGroup = group.fullScheduleGroup,
    fullGroup = group.fullGroup,
    playerCount = group.playerCount,
    classDays = classDays.asDomain()
)

@JvmName("ListGroupWithCalsDaysAsDomain")
fun List<GroupWithClassDaysEntity>.asDomain(): List<GroupWithClassDays> = map { it.asDomain() }

@JvmName("mapGroupEntityAsDomain")
fun Map<GroupEntity, List<ClassDayEntity>>.asDomain(): List<GroupWithClassDays> = map {
    GroupWithClassDays(
        id = it.key.id,
        name = it.key.name,
        days = it.key.days,
        explodeSchedules = it.key.explodeSchedules,
        fullScheduleGroup = it.key.fullScheduleGroup,
        fullGroup = it.key.fullGroup,
        playerCount = it.key.playerCount,
        classDays = it.value.asDomain()
    )
}

fun GroupEntity.asDomain(): Group = Group(
    id = id,
    name = name,
    days = days,
    explodeSchedules = explodeSchedules,
    fullScheduleGroup = fullScheduleGroup,
    fullGroup = fullGroup,
    playerCount = playerCount
)

fun GroupWithClassPlayersEntity.asDomain(): GroupWithClassPlayers = GroupWithClassPlayers(
    group = group.asDomain(),
    players = players.asDomain(),
    classDays = classDays.asDomain()
)

fun GroupWithPlayersEntity.asDomain(): GroupWithPlayers = GroupWithPlayers(
    id = group.id,
    name = group.name,
    days = group.days,
    explodeSchedules = group.explodeSchedules,
    fullScheduleGroup = group.fullScheduleGroup,
    fullGroup = group.fullGroup,
    playerCount = group.playerCount,
    players = players.asDomain()
)
