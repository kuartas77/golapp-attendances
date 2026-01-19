package com.golapp.attendances.data.datasources.remote

import com.golapp.attendances.data.di.IoDispatcher
import com.golapp.attendances.data.local.models.GroupWithClassPlayersEntity
import com.golapp.attendances.data.local.models.StatisticsEntity
import com.golapp.attendances.data.mappers.asEntity
import com.golapp.attendances.data.remote.GolappAPI
import com.golapp.attendances.data.remote.datasources.GroupsRemoteDataSource
import com.golapp.attendances.data.util.resultOf
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class GroupsRemoteDataSourceImpl @Inject constructor(
    private val api: GolappAPI,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GroupsRemoteDataSource {
    override suspend fun fetchGroups(): Flow<List<GroupWithClassPlayersEntity>> = flow {
        val result = resultOf { api.fetchGroups() }

        result.onSuccess { response ->
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(body.asEntity())
            } else {
                Timber.e("Fetch groups error: ${response.code()}")
                emit(emptyList())
            }
        }.onFailure {
            Timber.e(it, "Fetch groups failure")
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

    override suspend fun fetchGroup(groupId: Int): Flow<GroupWithClassPlayersEntity> = flow {
        val result = resultOf { api.fetchGroup(groupId) }

        result.onSuccess { response ->
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(body.asEntity())
            }
        }.onFailure {
            Timber.e(it, "Fetch group $groupId failure")
        }
    }.flowOn(ioDispatcher)

    override suspend fun fetchStatistics(): Flow<List<StatisticsEntity>> = flow {
        val result = resultOf { api.fetchStatistics() }

        result.onSuccess { response ->
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(body.asEntity())
            } else {
                emit(emptyList())
            }
        }.onFailure {
            Timber.e(it, "Fetch statistics failure")
            emit(emptyList())
        }
    }.flowOn(ioDispatcher)

}
