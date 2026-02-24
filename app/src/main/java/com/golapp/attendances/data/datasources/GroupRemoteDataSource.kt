package com.golapp.attendances.data.datasources

import com.golapp.attendances.domain.models.GroupWithClassPlayers

interface GroupRemoteDataSource {
    suspend fun fetchAllGroupsSnapshot(): List<GroupWithClassPlayers>
}