package com.golapp.attendances.data.remote.models.dtos

import com.google.gson.annotations.SerializedName

data class GroupDto(
    @SerializedName("days")
    val days: String,
    @SerializedName("explode_schedules")
    val explodeSchedules: String,
    @SerializedName("full_group")
    val fullGroup: String,
    @SerializedName("full_schedule_group")
    val fullScheduleGroup: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("player_count")
    val playerCount: Int,
    @SerializedName("class_days")
    val classDays: List<ClassDayDto>,
    @SerializedName("players")
    val players: List<PlayerDto> = emptyList()
)
