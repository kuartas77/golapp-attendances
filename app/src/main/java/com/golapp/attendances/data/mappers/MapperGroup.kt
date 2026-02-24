package com.golapp.attendances.data.mappers

import com.golapp.attendances.data.local.database.entities.ClassDayEntity
import com.golapp.attendances.data.local.database.entities.GroupEntity
import com.golapp.attendances.data.local.database.entities.GroupWithClassDaysEntity
import com.golapp.attendances.data.local.database.entities.GroupWithClassPlayersEntity
import com.golapp.attendances.data.local.database.entities.GroupWithPlayersEntity
import com.golapp.attendances.data.local.database.entities.PlayerEntity
import com.golapp.attendances.data.remote.models.dtos.GroupDto
import com.golapp.attendances.data.remote.models.dtos.toDomain
import com.golapp.attendances.domain.models.Group
import com.golapp.attendances.domain.models.GroupWithClassDays
import com.golapp.attendances.domain.models.GroupWithClassPlayers
import com.golapp.attendances.domain.models.GroupWithPlayers

fun GroupEntity.toDomain(): Group =
    Group(
        id = id,
        name = name,
        days = days,
        explodeSchedules = explodeSchedules,
        fullScheduleGroup = fullScheduleGroup,
        fullGroup = fullGroup,
        playerCount = playerCount
    )

fun Group.toEntity(): GroupEntity =
    GroupEntity(
        id = id,
        name = name,
        days = days,
        explodeSchedules = explodeSchedules,
        fullScheduleGroup = fullScheduleGroup,
        playerCount = playerCount,
        fullGroup = fullGroup
    )

fun GroupDto.toDomain(): GroupWithClassPlayers =
    GroupWithClassPlayers(
        group = Group(
            id = id,
            name = name,
            playerCount = playerCount,
            days = days,
            explodeSchedules = explodeSchedules,
            fullGroup = fullGroup,
            fullScheduleGroup = fullScheduleGroup,
        ),
        players = players.map { it.toDomain() },
        classDays = classDays.map { it.toDomain() },
    )

fun GroupWithClassDaysEntity.toDomain(filterMonth: Int? = null): GroupWithClassDays =
    GroupWithClassDays(
        group = group.toDomain(),
        classDays = classDays
            .let { list -> if (filterMonth == null) list else list.filter { it.month == filterMonth } }
            .map { it.toDomain() }
    )

fun GroupWithPlayersEntity.toDomain(): GroupWithPlayers =
    GroupWithPlayers(
        group = group.toDomain(),
        players = players.map { it.toDomain() }
    )

fun GroupWithClassPlayersEntity.toDomain(filterMonth: Int? = null): GroupWithClassPlayers =
    GroupWithClassPlayers(
        group = group.toDomain(),
        classDays = classDays
            .let { list -> if (filterMonth == null) list else list.filter { it.month == filterMonth } }
            .map { it.toDomain() },
        players = players.map { it.toDomain() }
    )

/**
 * Mapper clave para sync remoto → local (upsert grafo completo).
 * Esto produce:
 * - GroupEntity
 * - ClassDayEntity (amarradas al groupId del group)
 * - PlayerEntity (amarrados al groupId del group, por si acaso)
 */
data class GroupGraphEntities(
    val group: GroupEntity,
    val classDays: List<ClassDayEntity>,
    val players: List<PlayerEntity>
)

fun GroupWithClassPlayers.toGraphEntities(): GroupGraphEntities {
    val groupId = group.id

    return GroupGraphEntities(
        group = group.toEntity(),
        classDays = classDays.map { cd ->
            // Forzamos groupId consistente
            cd.copy(groupId = groupId).toEntity()
        },
        players = players
            .map { p -> p.toEntity(groupIdOverride = groupId) }
            .distinctBy { it.playerId }
    )
}
