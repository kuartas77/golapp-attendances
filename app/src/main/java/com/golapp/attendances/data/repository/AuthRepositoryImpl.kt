package com.golapp.attendances.data.repository

import androidx.annotation.WorkerThread
import com.golapp.attendances.common.di.IoDispatcher
import com.golapp.attendances.common.remote.NetworkResult
import com.golapp.attendances.common.resultOf
import com.golapp.attendances.data.local.datasources.StoreLocalDataSource
import com.golapp.attendances.data.remote.datasources.AuthRemoteDataSource
import com.golapp.attendances.domain.models.ResultLogin
import com.golapp.attendances.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
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
        resultOf {
            val responseLogin = authRemoteDataSource.authenticate(email, password)

            if(responseLogin.expires == 0L){
                emit(ResultLogin(message = responseLogin.message, code = responseLogin.code))
            } else{
                storeLocalDataSource.clear()
                storeLocalDataSource.saveToken(responseLogin.token)
                storeLocalDataSource.saveType(responseLogin.type)
                storeLocalDataSource.saveExpiration(responseLogin.expires)
                storeLocalDataSource.saveUserName(responseLogin.userDto?.name ?: "")
                storeLocalDataSource.saveSchoolId(responseLogin.userDto?.schoolId ?: 0)
                storeLocalDataSource.saveSchoolName(responseLogin.userDto?.schoolName ?: "")
                storeLocalDataSource.saveSchoolSlug(responseLogin.userDto?.schoolSlug ?: "")
                storeLocalDataSource.saveSchoolLogo(responseLogin.userDto?.schoolLogo ?: "")
                emit(ResultLogin(idle = true, code =  200))
            }
        }.onFailure {
            emit(ResultLogin(message = it.message))
        }
    }.onStart { ResultLogin() }.flowOn(ioDispatcher)

    override suspend fun validateTokenExpiry(): Boolean {
        val now = Date()
        val expirationDate = Date(storeLocalDataSource.getExpiration())
        val isAfter = expirationDate.after(now)

        return isAfter
    }

    override suspend fun logout() {
        storeLocalDataSource.clear()
    }
}
