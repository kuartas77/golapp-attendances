package com.golapp.attendances.data.remote.interceptors

import com.golapp.attendances.data.util.remote.NetworkMonitor
import com.golapp.attendances.data.util.remote.SuspendInterceptor
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class NetworkMonitorInterceptor @Inject constructor(
    private val networkMonitor: NetworkMonitor
) : SuspendInterceptor() {
    override suspend fun interceptSuspend(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (!networkMonitor.isConnected()) {
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        }
        return chain.proceed(request)
    }
}
