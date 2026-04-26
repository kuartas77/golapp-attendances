package com.golapp.attendances.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkMonitor @Inject constructor(
    context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(readCurrentConnection())
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // No decidir aquí “true” directo.
            // Android recomienda esperar capacidades para evitar race conditions.
            _isConnected.value = readCurrentConnection()
        }

        override fun onLost(network: Network) {
            // Recalcular el estado real actual.
            _isConnected.value = readCurrentConnection()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            _isConnected.value = networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
        }
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    fun isConnectedNow(): Boolean = isConnected.value

    private fun readCurrentConnection(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}