package com.golapp.attendances.core.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.golapp.attendances.feature.main.GolAppState
import com.golapp.attendances.core.navigation.graphs.Attendances
import com.golapp.attendances.core.navigation.graphs.Home
import com.golapp.attendances.core.navigation.graphs.authenticationScreens
import com.golapp.attendances.core.navigation.graphs.groupsScreen
import com.golapp.attendances.core.navigation.graphs.homeScreen
import com.golapp.attendances.core.navigation.graphs.settingScreen

@Composable
fun GolappNavHost(
    appState: GolAppState,
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val mainViewModel = appState.mainViewModel
    SharedTransitionLayout {
        NavDisplay(
            backStack = appState.backStack,
            modifier = modifier,
            onBack = { appState.navigateBack() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                authenticationScreens(
                    onDetectLogin = {
                        appState.replaceStack(Home)
                    }
                )

                homeScreen(onLogout = {
                    mainViewModel.logout()
                })

                settingScreen(onLogout = {
                    mainViewModel.logout()
                })

                groupsScreen(
                    onNavigateBackHome = {
                        appState.replaceStack(Home)
                    },
                    onClickClassDay = {
                        appState.navigate(Attendances(it))
                    },
                    onShowSnackbar = onShowSnackbar
                )
            },
            sharedTransitionScope = this,
            transitionSpec = {
                // Slide in from right when navigating forward
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
            },
            popTransitionSpec = {
                // Slide in from left when navigating back
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                // Slide in from left when navigating back
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
        )
    }

}
