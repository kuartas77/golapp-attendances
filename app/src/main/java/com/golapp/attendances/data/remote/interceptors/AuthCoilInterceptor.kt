package com.golapp.attendances.data.remote.interceptors

import com.golapp.attendances.common.remote.SuspendInterceptor
import com.golapp.attendances.data.local.datasources.StoreLocalDataSource
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthCoilInterceptor @Inject constructor(
    private val storeLocalDataSource: StoreLocalDataSource
) : SuspendInterceptor() {
    override suspend fun interceptSuspend(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = addAuthHeader(request)
        return chain.proceed(request)
    }

    private suspend fun addAuthHeader(request: Request): Request {

        val type = storeLocalDataSource.getType()
        val token = storeLocalDataSource.getToken()
        return request.newBuilder()
            .addHeader("Authorization", "$type $token")
            .build()
    }
}
