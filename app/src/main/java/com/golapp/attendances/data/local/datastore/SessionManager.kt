package com.golapp.attendances.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.golapp.attendances.core.coroutines.rethrowIfCancellation
import com.golapp.attendances.data.remote.RefreshApi
import com.golapp.attendances.data.remote.models.dtos.toDomain
import com.golapp.attendances.domain.models.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val preferenceDatasource: DataStore<Preferences>,
    private val refreshApi: dagger.Lazy<RefreshApi>,
    private val gson: Gson
) {
    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_KEY = stringPreferencesKey("user")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val TOKEN_TYPE = stringPreferencesKey("token_type")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val EXPIRATION_TOKEN_KEY = longPreferencesKey("expiration_token")
    }

    val isUserLoggedIn: Flow<Boolean> = preferenceDatasource.data
        .map { it[IS_LOGGED_IN] ?: false }

    suspend fun saveSession(
        token: String,
        refreshToken: String,
        type: String,
        expiration: Long,
        user: User?
    ) {
        preferenceDatasource.updateData { prefs ->
            prefs.toMutablePreferences().apply {
                this[IS_LOGGED_IN] = true
                this[TOKEN_KEY] = token
                this[REFRESH_TOKEN_KEY] = refreshToken
                this[TOKEN_TYPE] = type
                this[EXPIRATION_TOKEN_KEY] = expiration

                if (user != null) {
                    this[USER_KEY] = gson.toJson(user)
                } else {
                    remove(USER_KEY)
                }
            }
        }
    }

    suspend fun getUserOnce(): User? = try {
        val prefs = preferenceDatasource.data.first()
        prefs[USER_KEY]?.let { gson.fromJson(it, User::class.java) }
    } catch (error: Exception) {
        error.rethrowIfCancellation()
        null
    }

    suspend fun getToken(): String =
        preferenceDatasource.data.first()[TOKEN_KEY] ?: ""

    suspend fun getRefreshToken(): String =
        preferenceDatasource.data.first()[REFRESH_TOKEN_KEY] ?: ""

    suspend fun getType(): String =
        preferenceDatasource.data.first()[TOKEN_TYPE] ?: "Bearer"

    suspend fun getExpiration(): Long =
        preferenceDatasource.data.first()[EXPIRATION_TOKEN_KEY] ?: 0L

    suspend fun clearSession() {
        preferenceDatasource.updateData { prefs ->
            prefs.toMutablePreferences().apply { clear() }
        }
    }

    suspend fun refreshTokenSafely(): Boolean {
        return try {
            val refreshToken = getRefreshToken()
            if (refreshToken.isBlank()) return false

            val response = refreshApi.get().refreshToken("Bearer $refreshToken")
            val body = response.body()

            if (response.isSuccessful && body != null) {
                saveSession(
                    token = body.token,
                    refreshToken = body.refreshToken,
                    type = body.type,
                    expiration = body.expires,
                    user = body.userDto?.toDomain()
                )
                true
            } else {
                false
            }
        } catch (error: Exception) {
            error.rethrowIfCancellation()
            false
        }
    }
}
