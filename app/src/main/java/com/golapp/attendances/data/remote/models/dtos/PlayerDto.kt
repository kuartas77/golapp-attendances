package com.golapp.attendances.data.remote.models.dtos

import com.golapp.attendances.domain.models.Player
import com.google.gson.annotations.SerializedName

data class PlayerDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("category")
    val category: String,
    @SerializedName("full_names")
    val fullNames: String,
    @SerializedName("last_names")
    val lastNames: String,
    @SerializedName("names")
    val names: String,
    @SerializedName("photo_url")
    val photoUrl: String,
    @SerializedName("unique_code")
    val uniqueCode: String,
    @SerializedName("group_id")
    val groupId: Int,
    @SerializedName("inscription_id")
    val inscriptionId: Int
)

fun PlayerDto.toDomain(): Player = Player(
    playerId = id,
    category = category,
    fullNames = fullNames,
    lastNames = lastNames,
    names = names,
    photoUrl = photoUrl,
    uniqueCode = uniqueCode,
    groupId = groupId,
    inscriptionId = inscriptionId
)

