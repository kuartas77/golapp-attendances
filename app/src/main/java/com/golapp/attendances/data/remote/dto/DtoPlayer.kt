package com.golapp.attendances.data.remote.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DtoPlayer(
    @field:Json(name = "category")
    val category: String,
    @field:Json(name = "full_names")
    val fullNames: String,
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "last_names")
    val lastNames: String,
    @field:Json(name = "names")
    val names: String,
    @field:Json(name = "photo_url")
    val photoUrl: String,
    @field:Json(name = "unique_code")
    val uniqueCode: String,
    @field:Json(name = "group_id")
    val groupId: Int,
    @field:Json(name = "inscription_id")
    val inscriptionId: Int
)
