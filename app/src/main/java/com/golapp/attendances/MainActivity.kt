package com.golapp.attendances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.golapp.attendances.common.remote.NetworkMonitor
import com.golapp.attendances.common.ui.GolAppState
import com.golapp.attendances.common.ui.rememberAppState
import com.golapp.attendances.ui.navigation.Destinations
import com.golapp.attendances.ui.navigation.attendanceGraph
import com.golapp.attendances.ui.navigation.graphs.GuestGraph
import com.golapp.attendances.ui.navigation.guestGraph
import com.golapp.attendances.ui.navigation.homeGraph
import com.golapp.attendances.ui.theme.GolappAttendancesTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.collections.forEach

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState = rememberAppState(networkMonitor = networkMonitor)
            GolappAttendancesTheme {
                MainScreen(appState)
            }
        }
    }
}

@Composable
fun MainScreen(
    appState: GolAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    val layoutType = if (appState.isGuestDestination) {
        NavigationSuiteType.None
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
    }

    val screens = remember {
        listOf(
            Destinations.Home,
            Destinations.Groups
        )
    }

    val notConnectedMessage = stringResource(R.string.not_connected)
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = Indefinite
            )
        }
    }

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = navigationSuiteItems(
            currentDestination = appState.currentDestination,
            navController = appState.navController,
            screens = screens
        )
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        ),
                    )
            ) {
                Surface(
                    modifier = modifier
                ) {
                    NavHost(
                        navController = appState.navController,
                        startDestination = GuestGraph.Screens
                    ) {
                        guestGraph(appState.navController)
                        homeGraph(appState.navController)
                        attendanceGraph(appState.navController)
                    }
                }
            }
        }
    }
}

@Composable
private fun navigationSuiteItems(
    currentDestination: NavDestination?,
    navController: NavHostController,
    screens: List<Destinations<out Any>>
): NavigationSuiteScope.() -> Unit = {
    screens.forEach { screen ->
        val isSelected =
            currentDestination?.hierarchy?.any { it.route == screen.route } == true
        item(
            selected = isSelected,
            enabled = !isSelected,
            onClick = {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = {
                Icon(
                    painter = painterResource(screen.icon),
                    contentDescription = stringResource(screen.label),
                    modifier = Modifier.height(24.dp)
                )
            },
            label = {
                Text(text = stringResource(screen.label))
            },
            alwaysShowLabel = isSelected
        )
    }
}

