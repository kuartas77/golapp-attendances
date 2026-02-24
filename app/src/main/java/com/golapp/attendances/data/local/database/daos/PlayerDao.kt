package com.golapp.attendances.data.local.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.golapp.attendances.data.local.database.entities.PlayerEntity

@Dao
interface PlayerDao {
    @Upsert
    suspend fun upsertPlayers(players: List<PlayerEntity>)
}