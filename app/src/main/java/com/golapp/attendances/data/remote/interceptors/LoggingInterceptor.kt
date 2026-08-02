package com.golapp.attendances.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        Timber.tag("API_REQUEST").d("URL: ${request.url}")
        Timber.tag("API_REQUEST").d("Method: ${request.method}")
        Timber.tag("API_REQUEST").d("Headers: ${request.headers}")

        val response = chain.proceed(request)

        val responseBody = response.body
        val responseBodyString = responseBody.string()

        Timber.tag("API_RESPONSE").d("Code: ${response.code}")
        Timber.tag("API_RESPONSE").d("Body: $responseBodyString")
        Timber.tag("API_RESPONSE").d("Headers: ${response.headers}")

        return response.newBuilder()
            .body(responseBodyString.toResponseBody(responseBody.contentType()))
            .build()
    }
}