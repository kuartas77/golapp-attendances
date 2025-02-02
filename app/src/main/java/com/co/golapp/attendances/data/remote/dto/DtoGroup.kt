package com.co.golapp.attendances.data.remote.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DtoGroup(
    @field:Json(name = "class_days")
    val classDays: List<DtoClassDay>,
    @field:Json(name = "days")
    val days: String,
    @field:Json(name = "explode_schedules")
    val explodeSchedules: String,
    @field:Json(name = "full_group")
    val fullGroup: String,
    @field:Json(name = "full_schedule_group")
    val fullScheduleGroup: String,
    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "player_count")
    val playerCount: Int,
    @field:Json(name = "players")
    val players: List<DtoPlayer> = emptyList()
)
