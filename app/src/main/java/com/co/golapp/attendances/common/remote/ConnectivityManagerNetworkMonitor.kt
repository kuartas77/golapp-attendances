package com.co.golapp.attendances.common.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.NetworkRequest.Builder
import androidx.compose.ui.util.trace
import androidx.core.content.getSystemService
import com.co.golapp.attendances.common.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ConnectivityManagerNetworkMonitor @Inject constructor(
    val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : NetworkMonitor {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isConnected(): Boolean {
        var hasOnline = false
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        if (capabilities != null) {
            hasOnline = when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        return (hasOnline && network != null)
    }

    override val isOnline: Flow<Boolean> = callbackFlow {
        trace("NetworkMonitor.callbackFlow") {
            val connectivityManager = context.getSystemService<ConnectivityManager>()
            if (connectivityManager == null) {
                channel.trySend(false)
                channel.close()
                return@callbackFlow
            }

            /**
             * The callback's methods are invoked on changes to *any* network matching the [NetworkRequest],
             * not just the active network. So we can simply track the presence (or absence) of such [Network].
             */
            val callback = object : ConnectivityManager.NetworkCallback() {

                private val networks = mutableSetOf<Network>()

                override fun onAvailable(network: Network) {
                    networks += network
                    channel.trySend(true)
                }

                override fun onLost(network: Network) {
                    networks -= network
                    channel.trySend(networks.isNotEmpty())
                }
            }

            trace("NetworkMonitor.registerNetworkCallback") {
                val request = Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                connectivityManager.registerNetworkCallback(request, callback)
            }

            /**
             * Sends the latest connectivity status to the underlying channel.
             */
            channel.trySend(connectivityManager.isCurrentlyConnected())

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
    }
        .flowOn(ioDispatcher)
        .conflate()

    @Suppress("DEPRECATION")
    private fun ConnectivityManager.isCurrentlyConnected() = when {
        true ->
            activeNetwork
                ?.let(::getNetworkCapabilities)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

        else -> activeNetworkInfo?.isConnected
    } == true
}
