package com.app.common.presentation.utils

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColorPalette = lightColors(
    primary = PeachOrange,
    primaryVariant = CoffeeBrown,
    secondary = CoffeeBrown,
    secondaryVariant = PeachOrange,
    background = SandBeige,
    surface = BoneWhite,
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = CharcoalBrown,
    onSurface = CharcoalBrown,
    onError = Color.White
)

private val DarkColorPalette = darkColors(
    primary = PeachOrange,
    primaryVariant = PeachOrange,
    secondary = PeachOrange,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    onError = Color.Black
)

private val AppTypography = Typography(
    defaultFontFamily = FontFamily.SansSerif,
    h4 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp, color = CharcoalBrown),
    h5 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp, color = CharcoalBrown),
    h6 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = 0.15.sp, color = CharcoalBrown),
    subtitle1 = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, letterSpacing = 0.15.sp, color = CharcoalBrown),
    body1 = TextStyle(fontSize = 14.sp, letterSpacing = 0.25.sp, color = TaupeGray),
    body2 = TextStyle(fontSize = 12.sp, letterSpacing = 0.25.sp, color = TaupeGray),
    button = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 1.25.sp),
    caption = TextStyle(fontSize = 12.sp, letterSpacing = 0.4.sp, color = TaupeGray)
)

@Composable
fun AppTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    
    val currentTypography = Typography(
        defaultFontFamily = FontFamily.SansSerif,
        h4 = AppTypography.h4.copy(color = colors.onSurface),
        h5 = AppTypography.h5.copy(color = colors.onSurface),
        h6 = AppTypography.h6.copy(color = colors.onSurface),
        subtitle1 = AppTypography.subtitle1.copy(color = colors.onSurface),
        body1 = AppTypography.body1.copy(color = colors.onSurface.copy(alpha = 0.8f)),
        body2 = AppTypography.body2.copy(color = colors.onSurface.copy(alpha = 0.8f)),
        button = AppTypography.button,
        caption = AppTypography.caption.copy(color = colors.onSurface.copy(alpha = 0.6f))
    )

    MaterialTheme(
        colors = colors,
        typography = currentTypography,
        content = content
    )
}
