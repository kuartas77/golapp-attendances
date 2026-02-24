package com.golapp.attendances.data.remote.interceptors

import com.golapp.attendances.common.NetworkMonitor
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class NetworkMonitorInterceptor @Inject constructor(
    private val networkMonitor: NetworkMonitor
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = if (!networkMonitor.isConnectedNow()) {
            chain.request().newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        } else chain.request()

        return chain.proceed(request)
    }
}