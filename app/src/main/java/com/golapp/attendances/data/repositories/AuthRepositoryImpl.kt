package com.golapp.attendances.data.repositories

import androidx.room.withTransaction
import com.golapp.attendances.data.datasources.AuthRemoteDataSource
import com.golapp.attendances.data.local.database.AttendancesDB
import com.golapp.attendances.data.local.database.daos.GroupDao
import com.golapp.attendances.data.local.datastore.SessionManager
import com.golapp.attendances.data.remote.models.dtos.toDomain
import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.models.User
import com.golapp.attendances.domain.repositories.AuthRepository
import com.golapp.attendances.domain.ui.LoginState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val sessionManager: SessionManager,
    private val db: AttendancesDB,
    private val groupDao: GroupDao,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {
    override suspend fun login(email: String, password: String): LoginState =
        withContext(ioDispatcher) {
            return@withContext try {
                val res = authRemoteDataSource.auth(email, password)

                if (res.expires == 0L) {
                    LoginState.Error(
                        message = res.message ?: "Credenciales inválidas",
                        code = res.code
                    )
                } else {
                    sessionManager.saveSession(
                        token = res.token,
                        refreshToken = res.refreshToken,
                        type = res.type,
                        expiration = res.expires,
                        user = res.userDto?.toDomain()
                    )
                    LoginState.Success
                }
            } catch (e: Exception) {
                LoginState.Error(message = e.message ?: "Error de autenticación")
            }
        }

    override suspend fun validateTokenExpiry(): Boolean {
        val expiration = sessionManager.getExpiration()
        return expiration > System.currentTimeMillis()
    }

    override suspend fun getUserData(): User =
        sessionManager.getUserOnce() ?: User(
            name = "",
            schoolId = 0,
            schoolLogo = "",
            schoolName = "",
            schoolSlug = ""
        )

    override suspend fun logout() {
        db.withTransaction {
            groupDao.deleteAllGroups()
            sessionManager.clearSession()
        }
    }

    override fun isLoggedIn(): Flow<Boolean> = sessionManager.isUserLoggedIn
}