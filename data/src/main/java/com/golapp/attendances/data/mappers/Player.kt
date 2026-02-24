package com.golapp.attendances.data.mapper

import com.golapp.attendances.data.local.models.PlayerEntity
import com.golapp.attendances.data.remote.dto.DtoPlayer
import com.golapp.attendances.domain.models.Player

fun DtoPlayer.asEntity(): PlayerEntity =
    PlayerEntity(
        playerId = id,
        groupId = groupId,
        uniqueCode = uniqueCode,
        names = names,
        lastNames = lastNames,
        category = category,
        fullNames = fullNames,
        photoUrl = photoUrl,
        inscriptionId = inscriptionId
    )

fun Player.asEntity(): PlayerEntity = PlayerEntity(
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

fun PlayerEntity.asDomain(): Player = Player(
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

@JvmName("ListPlayerAsEntity")
fun List<PlayerEntity>.asDomain(): List<Player> = map { it.asDomain() }

@JvmName("ListPlayerAsDomain")
fun List<Player>.asEntity(): List<PlayerEntity> = map { it.asEntity() }

@JvmName("ListPlayerDtoAsEntity")
fun List<DtoPlayer>.asEntity(): List<PlayerEntity> = map { it.asEntity() }
