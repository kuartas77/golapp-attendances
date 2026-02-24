package com.golapp.attendances.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.get
import androidx.navigation.navOptions
import androidx.tracing.trace
import com.golapp.attendances.common.NetworkMonitor
import com.golapp.attendances.ui.navigation.Destinations
import com.golapp.attendances.ui.navigation.graphs.Authentication
import com.golapp.attendances.ui.navigation.graphs.Home
import com.golapp.attendances.ui.navigation.graphs.navigateToGroups
import com.golapp.attendances.ui.navigation.graphs.navigateToHome
import com.golapp.attendances.ui.navigation.graphs.navigateToSettings
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
    val topLevelDestinations: List<Destinations> = Destinations.entries

    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            // Collect the currentBackStackEntryFlow as a state
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            // Fallback to previousDestination if currentEntry is null
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val currentTopLevelDestination: Destinations?
        @Composable get() {
            return Destinations.entries.firstOrNull { topLevelDestination ->
                currentDestination?.hasRoute(route = topLevelDestination.route) == true
            }
        }

    val isGuestDestination: Boolean
        @Composable get() = currentDestination?.hierarchy?.any { it.route == navController.graph[Authentication].route } == true

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
                // Home SIEMPRE queda en la pila
                popUpTo(Home) {
                    inclusive = false
                    saveState = false // ponlo true si quieres restaurar estado por tab
                }
                launchSingleTop = true
                restoreState = false // ponlo true si usas saveState=true
            }

            when (destinations) {
                Destinations.HOME -> navController.navigateToHome(topLevelNavOptions)
                Destinations.GROUPS -> navController.navigateToGroups(topLevelNavOptions)
                Destinations.SETTINGS -> navController.navigateToSettings(topLevelNavOptions)
            }
        }
    }
}
