package com.golapp.attendances.data.local.datasources

import com.golapp.attendances.data.local.models.PlayerEntity

interface PlayersLocalDataSource {
    suspend fun insertPlayer(player: PlayerEntity)
    suspend fun insertPlayerList(playerList: List<PlayerEntity>)
    suspend fun deletePlayers(): Int
    suspend fun deletePlayersByGroupId(groupId: Int)
}
