package com.app.presentation.utils

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColorPalette = darkColors(
    primary = Teal500,
    primaryVariant = Teal700,
    secondary = Amber400,
    secondaryVariant = Amber600,
    background = DarkBackground,
    surface = DarkSurface,
    error = ErrorRed,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onError = Color.Black
)

private val AppTypography = Typography(
    defaultFontFamily = FontFamily.SansSerif,
    h4 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp),
    h5 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    h6 = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = 0.15.sp),
    subtitle1 = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, letterSpacing = 0.15.sp),
    body1 = TextStyle(fontSize = 14.sp, letterSpacing = 0.25.sp),
    body2 = TextStyle(fontSize = 12.sp, letterSpacing = 0.25.sp),
    button = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 1.25.sp),
    caption = TextStyle(fontSize = 12.sp, letterSpacing = 0.4.sp)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = AppTypography,
        content = content
    )
}
