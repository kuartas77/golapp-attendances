package com.golapp.attendances.data.local.datasources

interface StoreLocalDataSource {
    suspend fun saveToken(token: String)
    suspend fun saveType(type: String)
    suspend fun saveExpiration(expiration: Long)
    suspend fun getToken(): String
    suspend fun getType(): String
    suspend fun getExpiration(): Long
}
