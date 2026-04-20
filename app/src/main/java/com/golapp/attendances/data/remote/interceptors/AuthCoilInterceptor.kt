package com.golapp.attendances.data.remote.interceptors

import com.golapp.attendances.data.local.datastore.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthCoilInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : SuspendInterceptor() {
    override suspend fun interceptSuspend(chain: Interceptor.Chain): Response {
        val type = sessionManager.getType()
        val token = sessionManager.getToken()
        val request = chain.request()
        val newRequest = request.newBuilder()
            .header("Authorization", "$type $token")
            .build()

        return chain.proceed(newRequest)
    }
}