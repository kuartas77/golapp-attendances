package com.golapp.attendances.data.util.remote

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    fun isConnected(): Boolean
    val isOnline: Flow<Boolean>
}
