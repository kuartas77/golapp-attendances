package com.golapp.attendances.ui.theme

import androidx.compose.ui.graphics.Color

// --- Brand base (Light) ---
val BrandNavy = Color(0xFF111D47)
val BrandGold = Color(0xFFFECA00)

// --- Dark theme: tonos más opacos (menos “vivos”) ---
val LightPrimaryContainer = Color(0xFFDDE4FF)
val LightOnPrimaryContainer = Color(0xFF08143A)
val LightSecondary = Color(0xFF735C00)
val LightSecondaryContainer = Color(0xFFFFE58A)
val LightOnSecondaryContainer = Color(0xFF241A00)
val LightTertiary = Color(0xFF315DA8)
val LightTertiaryContainer = Color(0xFFD9E2FF)
val LightBackground = Color(0xFFF8F9FF)
val LightSurfaceContainer = Color(0xFFEEF0F8)
val LightSurfaceVariant = Color(0xFFE2E5F0)
val LightOnSurface = Color(0xFF191B24)
val LightOnSurfaceVariant = Color(0xFF444754)
val LightOutline = Color(0xFF747784)
val LightOutlineVariant = Color(0xFFC4C6D0)

// Gold más cálido y apagado (en vez de #FFCC05 puro)
val DarkPrimary = Color(0xFFFFD84D)
val DarkOnPrimary = Color(0xFF3B2F00)
val DarkPrimaryContainer = Color(0xFF293866)
val DarkOnPrimaryContainer = Color(0xFFDDE4FF)

// Navy un poco “levantado” para que no sea negro puro
val DarkSecondary = Color(0xFFE5C343)
val DarkSecondaryContainer = Color(0xFF554500)
val DarkOnSecondaryContainer = Color(0xFFFFE58A)
val DarkTertiary = Color(0xFFADC6FF)
val DarkTertiaryContainer = Color(0xFF174585)

// Textos claros suaves (no blanco puro)
val DarkBackground = Color(0xFF090E1D)
val DarkSurface = Color(0xFF101729)
val DarkSurfaceContainer = Color(0xFF18213A)
val DarkSurfaceVariant = Color(0xFF252E46)
val DarkOnSurface = Color(0xFFE4E7F2)
val DarkOnSurfaceVariant = Color(0xFFC4C6D0)
val DarkOutline = Color(0xFF8E909D)
val DarkOutlineVariant = Color(0xFF444754)

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
