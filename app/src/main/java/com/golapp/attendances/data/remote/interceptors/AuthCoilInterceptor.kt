package com.golapp.attendances.data.remote.interceptors

import com.golapp.attendances.data.util.remote.SuspendInterceptor
import com.golapp.attendances.data.local.datasources.StoreLocalDataSource
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthCoilInterceptor @Inject constructor(
    private val storeLocalDataSource: StoreLocalDataSource
) : SuspendInterceptor() {
    override suspend fun interceptSuspend(chain: Interceptor.Chain): Response {
        val type = storeLocalDataSource.getType()
        val token = storeLocalDataSource.getToken()
        val request = chain.request()
        val newRequest = request.newBuilder()
            .addHeader("Authorization", "$type $token")
            .build()

        return chain.proceed(newRequest)
    }
}
