package com.co.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupWithPlayers(
    override val id: Int,
    override val name: String,
    override val days: String,
    override val explodeSchedules: String,
    override val fullScheduleGroup: String,
    override val fullGroup: String,
    override val playerCount: Int,
    val players: List<Player>
) : IGroup, Parcelable
