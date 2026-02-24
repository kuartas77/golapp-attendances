package com.golapp.attendances.data.mappers

import com.golapp.attendances.data.local.database.entities.PlayerEntity
import com.golapp.attendances.data.remote.models.dtos.PlayerDto
import com.golapp.attendances.domain.models.Player
import com.golapp.attendances.domain.models.PlayerInGroup

fun PlayerDto.toDomain(): Player = Player(
    playerId = id,
    groupId = groupId,
    uniqueCode = uniqueCode,
    names = names,
    lastNames = lastNames,
    fullNames = fullNames,
    category = category,
    photoUrl = photoUrl,
    inscriptionId = inscriptionId
)

fun PlayerEntity.toDomain(): Player =
    Player(
        playerId = playerId,
        groupId = groupId,
        uniqueCode = uniqueCode,
        names = names,
        lastNames = lastNames,
        category = category,
        fullNames = fullNames,
        photoUrl = photoUrl,
        inscriptionId = inscriptionId
    )

fun Player.toEntity(groupIdOverride: Int? = null): PlayerEntity =
    PlayerEntity(
        playerId = playerId,
        groupId = groupIdOverride ?: groupId,
        uniqueCode = uniqueCode,
        names = names,
        lastNames = lastNames,
        category = category,
        fullNames = fullNames,
        photoUrl = photoUrl,
        inscriptionId = inscriptionId
    )

fun PlayerEntity.toPlayerInGroup(): PlayerInGroup =
    PlayerInGroup(playerId = playerId, inscriptionId = inscriptionId)