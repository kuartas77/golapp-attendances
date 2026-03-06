package com.golapp.attendances.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// --- Brand base (Light) ---
val Navy = Color(0xFF101E42)
val Gold = Color(0xFFFFCC05)

// --- Dark theme: tonos más opacos (menos “vivos”) ---
val DarkBg = Color(0xFF0B1229)
val DarkSurface = Color(0xFF0F1A3A)
val DarkSurface2 = Color(0xFF121F45)

// Gold más cálido y apagado (en vez de #FFCC05 puro)
val GoldMuted = Color(0xFFD8B24A)

// Navy un poco “levantado” para que no sea negro puro
val NavyMuted = Color(0xFF1B2A5A)

// Textos claros suaves (no blanco puro)
val DarkText = Color(0xFFE9E4D2)
val DarkTextMuted = Color(0xFFCFC8B2)
data class CustomTheme(
    val surface: Color,
    val surfaceLight: Color,
    val textPrimary: Color,
    val textInverse: Color,
    val iconPrimary: Color,
    val iconInverse: Color,
    val borderPrimary: Color,
    val borderError: Color,
    val buttonPrimary: Color,
    val buttonDisabled: Color
)

val lightThemeColors = CustomTheme(
    surface = Color.Gray,
    surfaceLight = Color(0xFFFAFAFA),
    textPrimary = Color.Black,
    textInverse = Color.White,
    iconPrimary = Color.Black,
    iconInverse = Color.White,
    borderPrimary = Color(0xFF101E42),
    borderError = Color(0xFFBD0000),
    buttonPrimary = Color(0xFF101E42),
    buttonDisabled = Color(0xFFDDDDDD)
)

val darkThemeColors = CustomTheme(
    surface = Color(0xFF000000),
    surfaceLight = Color(0xFF232323),
    textPrimary = Color.White,
    textInverse = Color.Black,
    iconPrimary = Color.White,
    iconInverse = Color.Black,
    borderPrimary = Color(0xFFFFCC05),
    borderError = Color(0xFFFF6E70),
    buttonPrimary = Color(0xFFFFCC05),
    buttonDisabled = Color(0xFFDDDDDD)
)

//val LocalTheme = staticCompositionLocalOf<CustomTheme> {
//    error("No theme specified")
//}