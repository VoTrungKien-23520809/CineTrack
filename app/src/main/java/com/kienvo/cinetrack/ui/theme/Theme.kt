package com.kienvo.cinetrack.ui.theme

import android.app.Activity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary          = CinemaRed,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF5C0007),
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary        = CinemaGold,
    onSecondary      = Color(0xFF3A2F00),
    background       = DarkBg,
    onBackground     = OnDark,
    surface          = DarkSurface,
    onSurface        = OnDark,
    surfaceVariant   = DarkSurface2,
    onSurfaceVariant = OnDarkSecond,
    error            = ErrorRed,
    outline          = Color(0xFF444444)
)

private val LightColorScheme = lightColorScheme(
    primary          = CinemaRed,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF3A0007),
    secondary        = Color(0xFFB8860B),
    onSecondary      = Color.White,
    background       = Color(0xFFF5F5F5),
    onBackground     = Color(0xFF1A1A1A),
    surface          = Color(0xFFFFFFFF),
    onSurface        = Color(0xFF1A1A1A),
    surfaceVariant   = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF555555),
    error            = ErrorRed,
    outline          = Color(0xFFAAAAAA)
)

@Composable
fun CineTrackTheme(isDark: Boolean = true, content: @Composable () -> Unit) {
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}