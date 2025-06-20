package com.golapp.attendances.data.datasources.remote

import com.golapp.attendances.common.di.IoDispatcher
import com.golapp.attendances.common.resultOf
import com.golapp.attendances.data.local.models.GroupWithClassPlayersEntity
import com.golapp.attendances.data.local.models.StatisticsEntity
import com.golapp.attendances.data.mappers.asEntity
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.datasources.GroupsRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GroupsRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GroupsRemoteDataSource {
    override suspend fun fetchGroups(): Flow<List<GroupWithClassPlayersEntity>> = flow {
        resultOf {
            val response = api.fetchGroups()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(body.asEntity())
            } else {
                emit(emptyList())
            }
        }.onFailure { emit(emptyList()) }
    }.flowOn(ioDispatcher)

    override suspend fun fetchGroup(groupId: Int): Flow<GroupWithClassPlayersEntity> = flow {
        resultOf {
            val response = api.fetchGroup(groupId)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(body.asEntity())
            }
        }
    }.flowOn(ioDispatcher)

    override suspend fun fetchStatistics(): Flow<List<StatisticsEntity>> = flow {
        resultOf {
            val response = api.fetchStatistics()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(body.asEntity())
            }
        }.onFailure {
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

}
