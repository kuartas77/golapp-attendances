package com.golapp.attendances.data.remote.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DtoGroup(
    @SerialName("class_days")
    val classDays: List<DtoClassDay>,
    @SerialName("days")
    val days: String,
    @SerialName("explode_schedules")
    val explodeSchedules: String,
    @SerialName("full_group")
    val fullGroup: String,
    @SerialName("full_schedule_group")
    val fullScheduleGroup: String,
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("player_count")
    val playerCount: Int,
    @SerialName("players")
    val players: List<DtoPlayer> = emptyList()
)
