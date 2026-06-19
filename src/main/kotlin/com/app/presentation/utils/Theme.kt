package com.app.presentation.utils

import androidx.compose.material.MaterialTheme
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
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        typography = AppTypography,
        content = content
    )
}
