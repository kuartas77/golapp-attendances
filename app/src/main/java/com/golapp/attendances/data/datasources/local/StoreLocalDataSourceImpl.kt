package com.golapp.attendances.data.datasources.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.golapp.attendances.common.Constants.EXPIRATION
import com.golapp.attendances.common.Constants.TOKEN
import com.golapp.attendances.common.Constants.TYPE
import com.golapp.attendances.data.local.datasources.StoreLocalDataSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StoreLocalDataSourceImpl @Inject constructor(
    private val preferenceDatasource: DataStore<Preferences>
) : StoreLocalDataSource {
    override suspend fun saveToken(token: String) {
        preferenceDatasource.updateData {
            it.toMutablePreferences().apply {
                set(stringPreferencesKey(TOKEN), token)
            }
        }
    }

    override suspend fun saveType(type: String) {
        preferenceDatasource.updateData {
            it.toMutablePreferences().apply {
                set(stringPreferencesKey(TYPE), type)
            }
        }
    }

    override suspend fun saveExpiration(expiration: Long) {
        preferenceDatasource.updateData {
            it.toMutablePreferences().apply {
                set(longPreferencesKey(EXPIRATION), expiration)
            }
        }
    }

    override suspend fun getToken(): String {
        return preferenceDatasource.data.first()[stringPreferencesKey(TOKEN)] ?: ""
    }

    override suspend fun getType(): String {
        return preferenceDatasource.data.first()[stringPreferencesKey(TYPE)] ?: ""
    }

    override suspend fun getExpiration(): Long {
        return preferenceDatasource.data.first()[longPreferencesKey(EXPIRATION)] ?: 0L
    }
}
