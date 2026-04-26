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
        ORDER BY
            CAST(SUBSTR(category, INSTR(category, '-') + 1) AS INTEGER) ASC
        """
    )
    suspend fun getPlayersByGroupIdOrderByCategoryNumber(groupId: Int): List<PlayerEntity>
}
