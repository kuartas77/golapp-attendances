package com.golapp.attendances.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.golapp.attendances.common.NetworkMonitor
import com.golapp.attendances.navigation.Destinations
import com.golapp.attendances.navigation.graphs.Authentication
import com.golapp.attendances.navigation.graphs.Home
import com.golapp.attendances.navigation.graphs.navigateToGroups
import com.golapp.attendances.navigation.graphs.navigateToHome
import com.golapp.attendances.navigation.graphs.navigateToSettings
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
    return remember(navController, coroutineScope, networkMonitor, mainViewModel) {
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
    val topLevelDestinations: List<Destinations> = Destinations.entries

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val isGuestDestination: Boolean
        @Composable get() = currentDestination?.hierarchy?.any { 
            it.route == navController.graph[Authentication].route 
        } == true

    val isOffline = networkMonitor.isConnected
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun navigateToDestination(destinations: Destinations) {
        trace("Navigation: ${destinations.name}") {
            val topLevelNavOptions = navOptions {
                popUpTo(Home) {
                    inclusive = false
                    saveState = false
                }
                launchSingleTop = true
                restoreState = false
            }

            when (destinations) {
                Destinations.HOME -> navController.navigateToHome(topLevelNavOptions)
                Destinations.GROUPS -> navController.navigateToGroups(topLevelNavOptions)
                Destinations.SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
            }
        }
    }
}
