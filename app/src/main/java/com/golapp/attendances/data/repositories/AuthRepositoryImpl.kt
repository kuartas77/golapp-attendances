package com.golapp.attendances.data.repositories

import androidx.room.withTransaction
import com.golapp.attendances.data.datasources.AuthRemoteDataSource
import com.golapp.attendances.data.local.database.AttendancesDB
import com.golapp.attendances.data.local.database.daos.GroupDao
import com.golapp.attendances.data.local.datastore.SessionManager
import com.golapp.attendances.data.remote.errors.AuthApiException
import com.golapp.attendances.data.remote.errors.NoNetworkException
import com.golapp.attendances.data.remote.models.dtos.toDomain
import com.golapp.attendances.di.IoDispatcher
import com.golapp.attendances.domain.models.User
import com.golapp.attendances.domain.repositories.AuthRepository
import com.golapp.attendances.domain.ui.LoginState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
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
                        reason = if (res.code == 401 || res.code == 422) {
                            LoginState.Reason.INVALID_CREDENTIALS
                        } else {
                            LoginState.Reason.UNKNOWN
                        },
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
            } catch (e: AuthApiException) {
                Timber.tag("AuthRepositoryImpl")
                    .w("Login failed with HTTP %s: %s", e.code, e.serverMessage)
                LoginState.Error(
                    reason = when (e.code) {
                        401, 422 -> LoginState.Reason.INVALID_CREDENTIALS
                        in 500..599 -> LoginState.Reason.SERVER
                        else -> LoginState.Reason.UNKNOWN
                    },
                    code = e.code
                )
            } catch (e: NoNetworkException) {
                LoginState.Error(reason = LoginState.Reason.CONNECTION)
            } catch (e: IOException) {
                LoginState.Error(reason = LoginState.Reason.CONNECTION)
            } catch (e: Exception) {
                Timber.tag("AuthRepositoryImpl").e(e, "Unexpected login error")
                LoginState.Error(reason = LoginState.Reason.UNKNOWN)
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
