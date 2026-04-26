package com.golapp.attendances.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object BrandDefaults {

    // --- Buttons ---
    @Composable
    fun primaryButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.55f)
    )

    @Composable
    fun secondaryButtonColors(): ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f),
        disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.55f)
    )

    // --- TopAppBar ---
    @Composable
    fun topAppBarColors(): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
        actionIconContentColor = MaterialTheme.colorScheme.onSurface
    )

    /** Fondo/colores base del contenedor (bar/rail/drawer) */
    /** Fondo/colores del contenedor (aplica a bar/rail/drawer) */
    @Composable
    fun navigationSuiteColors(): NavigationSuiteColors =
        NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.surface,
            navigationBarContentColor = MaterialTheme.colorScheme.onSurface,

            navigationRailContainerColor = MaterialTheme.colorScheme.surface,
            navigationRailContentColor = MaterialTheme.colorScheme.onSurface,

            navigationDrawerContainerColor = MaterialTheme.colorScheme.surface,
            navigationDrawerContentColor = MaterialTheme.colorScheme.onSurface,
        )

    /** Colores de ítems (selected/unselected/indicator) para bar/rail/drawer */
    @Composable
    fun navigationSuiteItemColors(): NavigationSuiteItemColors {
        val indicator =
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f) // suave en dark y light
        val selectedIcon = MaterialTheme.colorScheme.primary
        val selectedText = MaterialTheme.colorScheme.onSurface
        val unselected = MaterialTheme.colorScheme.onSurfaceVariant

        return NavigationSuiteDefaults.itemColors(
            navigationBarItemColors = NavigationBarItemDefaults.colors(
                indicatorColor = indicator,
                selectedIconColor = selectedIcon,
                selectedTextColor = selectedText,
                unselectedIconColor = unselected,
                unselectedTextColor = unselected,
            ),
            navigationRailItemColors = NavigationRailItemDefaults.colors(
                indicatorColor = indicator,
                selectedIconColor = selectedIcon,
                selectedTextColor = selectedText,
                unselectedIconColor = unselected,
                unselectedTextColor = unselected,
            ),
            navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = indicator,
                unselectedContainerColor = Color.Transparent,
                selectedIconColor = selectedIcon,
                unselectedIconColor = unselected,
                selectedTextColor = selectedText,
                unselectedTextColor = unselected,
            )
        )
    }

    // --- Cards ---
    @Composable
    fun cardColors(): CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface
    )

    @Composable
    fun elevatedCardColors(): CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}