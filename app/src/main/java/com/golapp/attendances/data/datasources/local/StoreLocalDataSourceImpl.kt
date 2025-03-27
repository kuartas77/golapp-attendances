package com.golapp.attendances.data.datasources.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.golapp.attendances.common.Constants.EXPIRATION
import com.golapp.attendances.common.Constants.SCHOOL_ID
import com.golapp.attendances.common.Constants.SCHOOL_LOGO
import com.golapp.attendances.common.Constants.SCHOOL_NAME
import com.golapp.attendances.common.Constants.SCHOOL_SLUG
import com.golapp.attendances.common.Constants.TOKEN
import com.golapp.attendances.common.Constants.TYPE
import com.golapp.attendances.common.Constants.USERNAME
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

    override suspend fun saveUserName(name: String) {
        preferenceDatasource.updateData {
            it.toMutablePreferences().apply {
                set(stringPreferencesKey(USERNAME), name)
            }
        }
    }

    override suspend fun saveSchoolId(schoolId: Int) {
        preferenceDatasource.updateData {
            it.toMutablePreferences().apply {
                set(intPreferencesKey(SCHOOL_ID), schoolId)
            }
        }
    }

    override suspend fun saveSchoolName(schoolName: String) {
        preferenceDatasource.updateData {
            it.toMutablePreferences().apply {
                set(stringPreferencesKey(SCHOOL_NAME), schoolName)
            }
        }
    }

    override suspend fun saveSchoolSlug(schoolSlug: String) {
        preferenceDatasource.updateData {
            it.toMutablePreferences().apply {
                set(stringPreferencesKey(SCHOOL_SLUG), schoolSlug)
            }
        }
    }

    override suspend fun saveSchoolLogo(schoolLogo: String) {
        preferenceDatasource.updateData {
            it.toMutablePreferences().apply {
                set(stringPreferencesKey(SCHOOL_LOGO), schoolLogo)
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

    override suspend fun getUserName(): String {
        return preferenceDatasource.data.first()[stringPreferencesKey(USERNAME)] ?: ""
    }

    override suspend fun getSchoolId(): Int {
        return preferenceDatasource.data.first()[intPreferencesKey(SCHOOL_ID)] ?: 0
    }

    override suspend fun getSchoolName(): String {
        return preferenceDatasource.data.first()[stringPreferencesKey(SCHOOL_NAME)] ?: ""
    }

    override suspend fun getSchoolSlug(): String {
        return preferenceDatasource.data.first()[stringPreferencesKey(SCHOOL_SLUG)] ?: ""
    }

    override suspend fun getSchoolLogo(): String {
        return preferenceDatasource.data.first()[stringPreferencesKey(SCHOOL_LOGO)] ?: ""
    }

    override suspend fun clear() {
        preferenceDatasource.updateData {
            it.toMutablePreferences().apply {
                clear()
            }
        }
    }
}
