package com.golapp.attendances.data.local.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.golapp.attendances.data.local.database.entities.PlayerEntity

@Dao
interface PlayerDao {
    @Upsert
    suspend fun upsertPlayers(players: List<PlayerEntity>)

    @Query(
        """
        SELECT * FROM players
        WHERE group_id = :groupId
        ORDER BY LENGTH(category) ASC, category ASC
        """
    )
    suspend fun getPlayersByGroupIdOrderByCategoryNumber(groupId: Int): List<PlayerEntity>
}
