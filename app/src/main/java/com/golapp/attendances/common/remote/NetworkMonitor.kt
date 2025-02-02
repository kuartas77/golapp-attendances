package com.golapp.attendances.common.remote

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {
    fun isConnected(): Boolean
    val isOnline: Flow<Boolean>
}
