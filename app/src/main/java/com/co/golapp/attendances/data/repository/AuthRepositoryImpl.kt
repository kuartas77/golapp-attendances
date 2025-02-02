package com.co.golapp.attendances.data.repository

import androidx.annotation.WorkerThread
import com.co.golapp.attendances.common.di.IoDispatcher
import com.co.golapp.attendances.data.local.datasources.StoreLocalDataSource
import com.co.golapp.attendances.common.remote.NetworkResult
import com.co.golapp.attendances.data.remote.datasources.AuthRemoteDataSource
import com.co.golapp.attendances.domain.models.ResultLogin
import com.co.golapp.attendances.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import java.util.Date
import javax.inject.Inject

@WorkerThread
class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val storeLocalDataSource: StoreLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String
    ): Flow<ResultLogin> = flow {
        when (val responseLogin = authRemoteDataSource.authenticate(email, password)) {
            is NetworkResult.Error -> emit(
                ResultLogin(
                    idle = false,
                    code = responseLogin.code,
                    message = responseLogin.message
                )
            )

            is NetworkResult.Exception -> emit(
                ResultLogin(
                    idle = false,
                    message = responseLogin.e.message
                )
            )

            is NetworkResult.Success -> {
                storeLocalDataSource.saveToken(responseLogin.data.token)
                storeLocalDataSource.saveType(responseLogin.data.type)
                storeLocalDataSource.saveExpiration(responseLogin.data.expires)
                emit(ResultLogin(idle = true, code = 200))
            }
        }
    }.onStart { ResultLogin() }.flowOn(ioDispatcher)

    override suspend fun validateTokenExpiry(): Boolean {
        val now = Date()
        val expirationDate = Date(storeLocalDataSource.getExpiration())
        val isAfter = expirationDate.after(now)

        return isAfter
    }
}
