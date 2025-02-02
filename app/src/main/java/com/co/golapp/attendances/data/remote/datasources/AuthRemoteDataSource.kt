package com.co.golapp.attendances.data.remote.datasources

import com.co.golapp.attendances.common.remote.NetworkResult
import com.co.golapp.attendances.data.remote.dto.ResponseLogin

interface AuthRemoteDataSource {
    suspend fun authenticate(email: String, password: String): NetworkResult<ResponseLogin>
}
