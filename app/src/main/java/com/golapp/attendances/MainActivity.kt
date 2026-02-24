package com.golapp.attendances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.golapp.attendances.common.NetworkMonitor
import com.golapp.attendances.ui.GolAppState
import com.golapp.attendances.ui.HeaderContent
import com.golapp.attendances.ui.navigation.GolappNavHost
import com.golapp.attendances.ui.rememberAppState
import com.golapp.attendances.ui.screens.SplashViewModel
import com.golapp.attendances.ui.theme.GolappAttendancesTheme
import com.golapp.attendances.ui.theme.LocalTheme
import com.golapp.attendances.ui.theme.darkThemeColors
import com.golapp.attendances.ui.theme.lightThemeColors
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.mapNotNull
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }

        setContent {

            val themeColors = if (isSystemInDarkTheme()) darkThemeColors else lightThemeColors

            val appState = rememberAppState(networkMonitor = networkMonitor)

            CompositionLocalProvider(LocalTheme provides themeColors) {
                GolappAttendancesTheme {
                    MainScreen(appState = appState)
                }
            }
        }
    }
}

@Composable
internal fun MainScreen(
    appState: GolAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
) {
    val currentDestination = appState.currentDestination

    val snackbarHostState = remember { SnackbarHostState() }

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    val layoutType = if (appState.isGuestDestination) {
        NavigationSuiteType.None
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(windowAdaptiveInfo)
    }

    val notConnectedMessage = stringResource(R.string.not_connected)

    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = SnackbarDuration.Long
            )
        }
    }

    if (BuildConfig.DEBUG) {
        LaunchedEffect(Unit) {
            appState.navController.currentBackStackEntryFlow.collect { entry ->
                val stack = appState.navController.currentBackStackEntryFlow
                    .mapNotNull { it.destination.route }
                Timber.tag("NAV").d("current=${entry.destination.route} stack=$stack")
            }
        }
    }

    GolApp(layoutType, appState, currentDestination, snackbarHostState, modifier)
}

@Composable
internal fun GolApp(
    layoutType: NavigationSuiteType,
    appState: GolAppState,
    currentDestination: NavDestination?,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteItems = { navigationItems(appState, currentDestination) }

    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .semantics {
                    testTagsAsResourceId = true
                },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(
                    snackbarHostState,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                )
            },
            topBar = {
                if (layoutType != NavigationSuiteType.None) {
                    HeaderContent()
                }
            }
        ) { padding ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal
                        )
                    )
            ) {
                Surface(
                    modifier = modifier
                ) {
                    GolappNavHost(
                        appState = appState,
                        onShowSnackbar = { message, action ->
                            snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = action,
                                duration = SnackbarDuration.Short,
                            ) == ActionPerformed
                        }
                    )
                }
            }
        }
    }
}


private fun NavigationSuiteScope.navigationItems(
    appState: GolAppState,
    currentDestination: NavDestination?
) {
    appState.topLevelDestinations.forEach { destination ->
        val isSelected =
            currentDestination.isRouteInHierarchy(destination.baseRoute)

        item(
            selected = isSelected,
            onClick = { appState.navigateToDestination(destination) },
            icon = {
                Icon(
                    painter = painterResource(destination.icon),
                    contentDescription = stringResource(destination.label),
                    modifier = Modifier.height(24.dp)
                )
            },
            label = {
                Text(text = stringResource(destination.label))
            },
            alwaysShowLabel = true,
        )
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any { it.hasRoute(route) } == true
