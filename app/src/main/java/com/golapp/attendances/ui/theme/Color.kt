package com.golapp.attendances.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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
    surface = Color(0xFFFFFFFF),
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

val LocalTheme = staticCompositionLocalOf<CustomTheme> {
    error("No theme specified")
}