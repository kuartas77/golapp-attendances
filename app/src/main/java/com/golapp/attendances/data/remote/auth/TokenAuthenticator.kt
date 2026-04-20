package com.golapp.attendances.data.remote.auth

import com.golapp.attendances.data.local.datastore.SessionManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val sessionManager: SessionManager
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Evita loops infinitos de reintentos
        if (responseCount(response) >= 2) {
            runBlocking {
                sessionManager.clearSession()
            }
            return null
        }

        synchronized(lock) {
            val currentToken = runBlocking { sessionManager.getToken() }
            val currentType = runBlocking { sessionManager.getType() }
            val currentAuthHeader = if (currentToken.isNotBlank()) {
                "$currentType $currentToken"
            } else {
                ""
            }

            val requestAuthHeader = response.request.header("Authorization")

            // Si otro hilo ya refrescó el token mientras esta request esperaba,
            // solo reconstruimos la request con el token nuevo.
            if (
                currentToken.isNotBlank() &&
                requestAuthHeader != null &&
                requestAuthHeader != currentAuthHeader
            ) {
                return response.request.newBuilder()
                    .header("Authorization", currentAuthHeader)
                    .build()
            }

            val refreshed = runBlocking {
                sessionManager.refreshTokenSafely()
            }

            if (!refreshed) {
                runBlocking {
                    sessionManager.clearSession()
                }
                return null
            }

            val newToken = runBlocking { sessionManager.getToken() }
            val newType = runBlocking { sessionManager.getType() }

            if (newToken.isBlank() || newType.isBlank()) {
                runBlocking {
                    sessionManager.clearSession()
                }
                return null
            }

            return response.request.newBuilder()
                .header("Authorization", "$newType $newToken")
                .build()
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var current: Response? = response.priorResponse

        while (current != null) {
            count++
            current = current.priorResponse
        }

        return count
    }
}