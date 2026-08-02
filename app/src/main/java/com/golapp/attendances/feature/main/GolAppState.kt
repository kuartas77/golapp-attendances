package com.golapp.attendances.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.tracing.trace
import com.golapp.attendances.core.common.NetworkMonitor
import com.golapp.attendances.core.navigation.Destinations
import com.golapp.attendances.core.navigation.graphs.Authentication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberAppState(
    networkMonitor: NetworkMonitor,
    startDestination: NavKey,
    backStack: MutableList<NavKey> = rememberNavBackStack(startDestination),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    mainViewModel: MainViewModel = hiltViewModel()
): GolAppState {
    return remember(backStack, coroutineScope, networkMonitor, mainViewModel) {
        GolAppState(
            backStack = backStack,
            mainViewModel = mainViewModel,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
        )
    }
}

@Stable
class GolAppState(
    val backStack: MutableList<NavKey>,
    val mainViewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
) {
    val topLevelDestinations: List<Destinations> = Destinations.entries

    val currentDestination: NavKey?
        get() = backStack.lastOrNull()

    val isGuestDestination: Boolean
        get() = currentDestination is Authentication

    val isOffline = networkMonitor.isConnected
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun navigateToDestination(destinations: Destinations) {
        trace("Navigation: ${destinations.name}") {
            replaceStack(destinations.route)
        }
    }

    fun replaceStack(vararg routes: NavKey) {
        backStack.clear()
        backStack.addAll(routes)
    }

    fun navigate(route: NavKey) {
        if (backStack.lastOrNull() != route) {
            backStack.add(route)
        }
    }

    fun navigateBack(): Boolean =
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            true
        } else {
            false
        }
}
