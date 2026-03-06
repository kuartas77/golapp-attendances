package com.golapp.attendances.ui.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Navy,
    onPrimary = Color.White,          // contraste: texto/iconos sobre primary
    secondary = Gold,
    onSecondary = Navy,        // contraste: texto/iconos sobre secondary

    background = Color(0xFFF7F8FC),
    onBackground = Navy,

    surface = Color.White,
    onSurface = Navy,

    surfaceVariant = Color(0xFFEEF1F9),
    onSurfaceVariant = Navy,

    outline = Color(0xFFCBD2E6)
)

private val DarkColorScheme = darkColorScheme(
    primary = GoldMuted,
    onPrimary = DarkBg,

    secondary = NavyMuted,
    onSecondary = DarkText,

    background = DarkBg,
    onBackground = DarkText,

    surface = DarkSurface,
    onSurface = DarkText,

    surfaceVariant = DarkSurface2,
    onSurfaceVariant = DarkTextMuted,

    outline = Color(0xFF3A4775)
)

@Composable
fun GolappAttendancesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && supportsDynamicTheming() -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
private fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
