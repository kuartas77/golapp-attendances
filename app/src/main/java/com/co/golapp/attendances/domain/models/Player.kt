package com.co.golapp.attendances.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    override val playerId: Int,
    override val groupId: Int,
    override val uniqueCode: String,
    override val names: String,
    override val lastNames: String,
    override val category: String,
    override val fullNames: String,
    override val photoUrl: String,
    override val inscriptionId: Int
) : IPlayer, Parcelable
