package com.example.mas_cake1.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CakeColorScheme = lightColorScheme(
    primary = ChocolateBrown,
    secondary = LightCaramel,
    background = CreamWhite,
    surface = CreamWhite,
    onPrimary = CreamWhite,
    onBackground = ChocolateBrown,
    onSurface = ChocolateBrown
)

@Composable
fun MAS_CAKE1Theme(
    content: @Composable () -> Unit
) {
    val colorScheme = CakeColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}