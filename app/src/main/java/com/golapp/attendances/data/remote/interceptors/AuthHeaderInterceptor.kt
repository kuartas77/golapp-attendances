package com.golapp.attendances.data.remote.interceptors

import com.golapp.attendances.data.local.datastore.SessionManager
import com.golapp.attendances.di.Authorized
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthHeaderInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : SuspendInterceptor() {

    override suspend fun interceptSuspend(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val invocation = originalRequest.tag(Invocation::class.java)
        val hasAuthorizedAnnotation = invocation
            ?.method()
            ?.annotations
            ?.any { it is Authorized } == true

        val request = if (hasAuthorizedAnnotation) {
            addAuthHeaders(originalRequest)
        } else {
            addDefaultHeaders(originalRequest)
        }

        return chain.proceed(request)
    }

    private suspend fun addAuthHeaders(request: Request): Request {
        val type = sessionManager.getType()
        val token = sessionManager.getToken()

        return request.newBuilder()
            .header("Authorization", "$type $token")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build()
    }

    private fun addDefaultHeaders(request: Request): Request {
        return request.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .build()
    }
}