package com.golapp.attendances.data.datasources.local

import com.golapp.attendances.data.local.dao.PlayerDao
import com.golapp.attendances.data.local.datasources.PlayersLocalDataSource
import com.golapp.attendances.data.local.models.PlayerEntity
import javax.inject.Inject

class PlayersLocalDataSourceImpl @Inject constructor(
    private val playerDao: PlayerDao
) : PlayersLocalDataSource {
    override suspend fun insertPlayer(player: PlayerEntity) = playerDao.insertPlayer(player)

    override suspend fun insertPlayerList(playerList: List<PlayerEntity>) =
        playerDao.insertPlayerList(playerList)

    override suspend fun deletePlayers() = playerDao.deletePlayers()
    override suspend fun deletePlayersByGroupId(groupId: Int) =
        playerDao.deletePlayersByGroupId(groupId)
}
