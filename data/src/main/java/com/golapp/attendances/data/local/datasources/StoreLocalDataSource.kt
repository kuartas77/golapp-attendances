package com.golapp.attendances.data.local.datasources

interface StoreLocalDataSource {
    suspend fun saveToken(token: String)
    suspend fun saveRefreshToken(refreshToken: String)
    suspend fun saveType(type: String)
    suspend fun saveExpiration(expiration: Long)
    suspend fun saveUserName(name: String)
    suspend fun saveSchoolId(schoolId: Int)
    suspend fun saveSchoolName(schoolName: String)
    suspend fun saveSchoolSlug(schoolSlug: String)
    suspend fun saveSchoolLogo(schoolLogo: String)
    suspend fun getToken(): String
    suspend fun getRefreshToken(): String
    suspend fun getType(): String
    suspend fun getExpiration(): Long
    suspend fun getUserName(): String
    suspend fun getSchoolId(): Int
    suspend fun getSchoolName(): String
    suspend fun getSchoolSlug(): String
    suspend fun getSchoolLogo(): String
    suspend fun clear()
    suspend fun refreshToken()
}
