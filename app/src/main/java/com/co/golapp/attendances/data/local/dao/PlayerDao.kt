package com.co.golapp.attendances.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.co.golapp.attendances.data.local.models.PlayerEntity

@Dao
interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(playerEntity: PlayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerList(playerList: List<PlayerEntity>)

    @Query("DELETE FROM players")
    suspend fun deletePlayers(): Int
}
