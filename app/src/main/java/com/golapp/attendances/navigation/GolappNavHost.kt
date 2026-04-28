package com.golapp.attendances.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.golapp.attendances.feature.main.GolAppState
import com.golapp.attendances.navigation.graphs.Attendances
import com.golapp.attendances.navigation.graphs.Authentication
import com.golapp.attendances.navigation.graphs.Home
import com.golapp.attendances.navigation.graphs.authenticationScreens
import com.golapp.attendances.navigation.graphs.groupsScreen
import com.golapp.attendances.navigation.graphs.homeScreen
import com.golapp.attendances.navigation.graphs.settingScreen

@Composable
fun GolappNavHost(
    appState: GolAppState,
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean
) {
    val mainViewModel = appState.mainViewModel
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
                appState.replaceStack(Authentication)
            })

            settingScreen(onLogout = {
                mainViewModel.logout()
                appState.replaceStack(Authentication)
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
        }
    )

}
