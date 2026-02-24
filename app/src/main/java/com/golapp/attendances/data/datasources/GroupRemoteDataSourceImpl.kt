package com.golapp.attendances.data.datasources

import com.golapp.attendances.data.mappers.toDomain
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.domain.models.GroupWithClassPlayers
import javax.inject.Inject

class GroupRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI
): GroupRemoteDataSource {

    override suspend fun fetchAllGroupsSnapshot(): List<GroupWithClassPlayers> {
        val res = api.getAllGroupsSnapshot()
        return res.data.orEmpty().map { it.toDomain() }
    }
}