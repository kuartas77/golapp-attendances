package com.golapp.attendances.data.remote.interceptors

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

abstract class SuspendInterceptor : Interceptor {
    final override fun intercept(chain: Interceptor.Chain): Response {
        return runBlocking {
            try {
                interceptSuspend(chain)
            } catch (ce: CancellationException) {
                throw IOException(ce)
            }
        }
    }

    abstract suspend fun interceptSuspend(chain: Interceptor.Chain): Response
}