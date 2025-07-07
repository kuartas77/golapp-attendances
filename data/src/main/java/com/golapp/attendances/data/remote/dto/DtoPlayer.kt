package com.golapp.attendances.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DtoPlayer(
    @SerialName("category")
    val category: String,
    @SerialName("full_names")
    val fullNames: String,
    @SerialName("id")
    val id: Int,
    @SerialName("last_names")
    val lastNames: String,
    @SerialName("names")
    val names: String,
    @SerialName("photo_url")
    val photoUrl: String,
    @SerialName("unique_code")
    val uniqueCode: String,
    @SerialName("group_id")
    val groupId: Int,
    @SerialName("inscription_id")
    val inscriptionId: Int
)
