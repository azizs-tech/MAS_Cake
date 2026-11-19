package com.example.mas_cake1.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/** Colours every screen uses, so the whole app can flip to dark mode at once. */
data class MasColors(
    val background: Color,
    val surface: Color,
    val text: Color,
    val textMuted: Color,
    val accent: Color,
    val primary: Color,
    val onPrimary: Color,
    val divider: Color,
    val shimmer: Color,
    val isDark: Boolean
)

val LightMasColors = MasColors(
    background = CreamWhite,
    surface = Color.White,
    text = ChocolateBrown,
    textMuted = ChocolateBrown.copy(alpha = 0.65f),
    accent = LightCaramel,
    primary = ChocolateBrown,
    onPrimary = CreamWhite,
    divider = Color(0xFFEDE3D6),
    shimmer = Color(0xFFE9E1D7),
    isDark = false
)

val DarkMasColors = MasColors(
    background = Espresso,
    surface = Mocha,
    text = MilkFoam,
    textMuted = MilkFoam.copy(alpha = 0.6f),
    accent = LightCaramel,
    primary = LightCaramel,
    onPrimary = Espresso,
    divider = Color(0xFF3A2C26),
    shimmer = Color(0xFF33261F),
    isDark = true
)

val LocalMasColors = staticCompositionLocalOf { LightMasColors }

/** true = English, false = Russian. Provided once at the top of the app. */
val LocalEnglish = compositionLocalOf { true }

object Mas {
    val colors: MasColors
        @Composable @ReadOnlyComposable get() = LocalMasColors.current
}

/** Pick the English or Russian text: tr("Cart", "Корзина") */
@Composable
@ReadOnlyComposable
fun tr(en: String, ru: String): String = if (LocalEnglish.current) en else ru

private val LightScheme = lightColorScheme(
    primary = ChocolateBrown,
    onPrimary = CreamWhite,
    secondary = LightCaramel,
    background = CreamWhite,
    surface = CreamWhite,
    surfaceContainerLow = Color.White,
    onBackground = ChocolateBrown,
    onSurface = ChocolateBrown,
    onSurfaceVariant = ChocolateBrown.copy(alpha = 0.7f),
    outline = Color(0xFFCFC3B5),
    secondaryContainer = ChocolateBrown,
    onSecondaryContainer = CreamWhite
)

private val DarkScheme = darkColorScheme(
    primary = LightCaramel,
    onPrimary = Espresso,
    secondary = LightCaramel,
    background = Espresso,
    surface = Espresso,
    surfaceContainerLow = Mocha,
    onBackground = MilkFoam,
    onSurface = MilkFoam,
    onSurfaceVariant = MilkFoam.copy(alpha = 0.7f),
    outline = Color(0xFF5A463C),
    secondaryContainer = LightCaramel,
    onSecondaryContainer = Espresso
)

@Composable
fun MAS_CAKE1Theme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }
    CompositionLocalProvider(LocalMasColors provides if (darkTheme) DarkMasColors else LightMasColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkScheme else LightScheme,
            typography = Typography,
            content = content
        )
    }
}
