package com.golapp.attendances.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.golapp.attendances.data.remote.GolappAPI
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
    private val api: dagger.Lazy<GolappAPI>,
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
                if (user != null) this[USER_KEY] = gson.toJson(user) else remove(USER_KEY)
            }
        }
    }

    suspend fun saveUser(user: User) {
        preferenceDatasource.updateData { prefs ->
            prefs.toMutablePreferences().apply { this[USER_KEY] = gson.toJson(user) }
        }
    }

    suspend fun getUserOnce(): User? = runCatching {
        val prefs = preferenceDatasource.data.first()
        prefs[USER_KEY]?.let { gson.fromJson(it, User::class.java) }
    }.getOrNull()

    suspend fun getToken(): String =
        preferenceDatasource.data.first()[TOKEN_KEY] ?: ""

    suspend fun getRefreshToken(): String =
        preferenceDatasource.data.first()[REFRESH_TOKEN_KEY] ?: ""

    suspend fun getType(): String =
        preferenceDatasource.data.first()[TOKEN_TYPE] ?: ""

    suspend fun getExpiration(): Long =
        preferenceDatasource.data.first()[EXPIRATION_TOKEN_KEY] ?: 0L

    suspend fun clearSession() {
        preferenceDatasource.updateData { prefs ->
            prefs.toMutablePreferences().apply { clear() }
        }
    }

    suspend fun refreshToken() {
        val refreshToken = getRefreshToken()

        val type: String = getType()

        val response = api.get().refreshToken("$type $refreshToken")

        val responseLogin = response.body()

        if (response.isSuccessful && responseLogin != null) {
            saveSession(
                token = responseLogin.token,
                refreshToken = responseLogin.refreshToken,
                type = responseLogin.type,
                expiration = responseLogin.expires,
                user = responseLogin.userDto?.toDomain()
            )
        }
    }
}