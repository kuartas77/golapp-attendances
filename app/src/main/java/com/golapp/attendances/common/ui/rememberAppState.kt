package com.golapp.attendances.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import com.golapp.attendances.common.remote.NetworkMonitor
import com.golapp.attendances.ui.navigation.graphs.GuestGraph
import com.golapp.attendances.ui.screens.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberAppState(
    networkMonitor: NetworkMonitor,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    mainViewModel: MainViewModel = hiltViewModel()
): GolAppState {
    return remember(navController, coroutineScope) {
        GolAppState(
            navController = navController,
            mainViewModel = mainViewModel,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
        )
    }
}

@Stable
class GolAppState(
    val navController: NavHostController,
    val mainViewModel: MainViewModel,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
) {
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isGuestDestination: Boolean
        @Composable get() = currentDestination?.hierarchy?.any { it.route == navController.graph[GuestGraph.Guest].route } == true

    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}
