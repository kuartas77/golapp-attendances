package com.golapp.attendances.data.remote.interceptors

import com.golapp.attendances.common.di.Authorized
import com.golapp.attendances.common.remote.SuspendInterceptor
import com.golapp.attendances.data.local.datasources.StoreLocalDataSource
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Invocation
import javax.inject.Inject

class AuthHeaderInterceptor @Inject constructor(
    private val storeLocalDataSource: StoreLocalDataSource
) : SuspendInterceptor() {
    override suspend fun interceptSuspend(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val invocation =
            chain.request().tag(Invocation::class.java) ?: return chain.proceed(chain.request())
        containedOnInvocation(invocation).forEach { annotation ->
            request = handleAnnotation(annotation, request)
        }
        return chain.proceed(request)
    }

    private fun containedOnInvocation(invocation: Invocation): Set<Annotation> {
        return invocation.method().annotations.toSet()
    }

    private suspend fun handleAnnotation(
        annotation: Annotation,
        request: Request,
    ): Request {
        return when (annotation) {
            is Authorized -> addHeaders(request)
            else -> addContentTypeHeader(request)
        }
    }

    private suspend fun addHeaders(request: Request): Request {

        val type = storeLocalDataSource.getType()
        val token = storeLocalDataSource.getToken()
        return request.newBuilder()
            .addHeader("Authorization", "$type $token")
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()
    }

    private fun addContentTypeHeader(request: Request): Request {
        return request.newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            .build()
    }
}
