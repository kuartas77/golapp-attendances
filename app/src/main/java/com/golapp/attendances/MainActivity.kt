package com.golapp.attendances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.golapp.attendances.core.common.NetworkMonitor
import com.golapp.attendances.feature.main.GolAppState
import com.golapp.attendances.feature.main.HeaderContent
import com.golapp.attendances.feature.main.MainViewModel
import com.golapp.attendances.feature.main.rememberAppState
import com.golapp.attendances.core.navigation.GolappNavHost
import com.golapp.attendances.core.navigation.graphs.Authentication
import com.golapp.attendances.ui.theme.BrandDefaults
import com.golapp.attendances.ui.theme.GolappAttendancesTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        viewModel.start()
        splashScreen.setKeepOnScreenCondition { viewModel.uiState.value.isLoading }

        setContent {

//            val themeColors = if (isSystemInDarkTheme()) darkThemeColors else lightThemeColors

    val appState = rememberAppState(networkMonitor = networkMonitor, mainViewModel = viewModel)
    val uiState by appState.mainViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLoading, uiState.isLoggedIn) {
        if (!uiState.isLoading) {
            appState.replaceStack(if (uiState.isLoggedIn) com.golapp.attendances.core.navigation.graphs.Home else Authentication)
        }
    }

    GolappAttendancesTheme {
        MainScreen(appState = appState, uiState = uiState)
    }
//            }
        }
    }
}

@Composable
internal fun MainScreen(
    appState: GolAppState,
    uiState: com.golapp.attendances.feature.home.HomeUiState,
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
        LaunchedEffect(currentDestination) {
            Timber.tag("NAV").d(
                "current=${currentDestination?.javaClass?.simpleName} stack=${appState.backStack}"
            )
        }
    }

    GolApp(layoutType, appState, currentDestination, uiState, snackbarHostState, modifier)
}

@Composable
internal fun GolApp(
    layoutType: NavigationSuiteType,
    appState: GolAppState,
    currentDestination: NavKey?,
    uiState: com.golapp.attendances.feature.home.HomeUiState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    val suiteColors = BrandDefaults.navigationSuiteColors()
    val suiteItemColors = BrandDefaults.navigationSuiteItemColors()

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteColors = suiteColors,
        navigationSuiteItems = {
            if (layoutType != NavigationSuiteType.None) {
                navigationItems(appState, currentDestination, suiteItemColors)
            }
        }

    ) {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .semantics {
                    testTagsAsResourceId = true
                },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(
                    snackbarHostState,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                )
            },
            topBar = {
                if (layoutType != NavigationSuiteType.None) {
                    HeaderContent(uiState = uiState)
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
                            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                        )
                    )
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


private fun NavigationSuiteScope.navigationItems(
    appState: GolAppState,
    currentDestination: NavKey?,
    suiteItemColors: NavigationSuiteItemColors
) {
    appState.topLevelDestinations.forEach { destination ->
        val isSelected = destination.isSelected(currentDestination)

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
            colors = suiteItemColors
        )
    }
}
